package fr.enedis.teme.assertapi.core;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import fr.enedis.teme.assertapi.core.ResponseComparator.SafeSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class DefaultApiAssertions implements ApiAssertions {
	
	private final RestTemplate exTemp;
	private final RestTemplate acTemp;
	private final ResponseComparator comparator;
	
	@Override
	public void assertApi(HttpQuery query) throws Exception {
		
		var comp = comparator.comparing(query);
		comp.assumeEnabled(query.isEnable());
    	String aUrl = query.getActual().uri();
    	CompletableFuture<ResponseEntity<byte[]>> af = query.isParallel() 
    			? supplyAsync(()-> acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().httpMethod()), null, byte[].class), commonPool())
    			: completedFuture(acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().httpMethod()), null, byte[].class));
    	try {
        	var eRes = exTemp.exchange(query.getExpected().uri(), HttpMethod.valueOf(query.getExpected().httpMethod()), null, byte[].class);
        	
        	var aRes = comp.assertNotResponseException(execute(af));
        	comp.assertStatusCode(eRes.getStatusCodeValue(), aRes.getStatusCodeValue());
        	comp.assertContentType(eRes.getHeaders().getContentType(), aRes.getHeaders().getContentType());
			if(isTextContent(eRes.getHeaders().getContentType())) {
		    	var eCont = new String(eRes.getBody(), query.getExpected().charset());
		    	var aCont = new String(aRes.getBody(), query.getActual().charset());
		    	if(APPLICATION_JSON.isCompatibleWith(eRes.getHeaders().getContentType())) {
		    		comp.assertJsonContent(
							excludePaths(eCont, query.getExpected()),
							excludePaths(aCont, query.getActual()), 
							query.isStrict());
		    	}
		    	else {
		    		comp.assertTextContent(eCont, aCont);
		    	}
			}
			else {
				comp.assertByteContent(eRes.getBody(), aRes.getBody());
			}
    	}
    	catch(RestClientResponseException ee) {
        	var ae = comp.assertResponseException(execute(af));
        	comp.assertStatusCode(ee.getRawStatusCode(), ae.getRawStatusCode());
        	comp.assertContentType(ee.getResponseHeaders().getContentType(), ae.getResponseHeaders().getContentType());
        	var mediaType = ee.getResponseHeaders().getContentType();
			if(isTextContent(mediaType)) {
	        	if(APPLICATION_JSON.isCompatibleWith(mediaType)) {
		    		comp.assertJsonContent(
							excludePaths(ee.getResponseBodyAsString(), query.getExpected()),
							excludePaths(ae.getResponseBodyAsString(), query.getActual()), 
							query.isStrict());
		    	}
		    	else {
		    		comp.assertTextContent(ee.getResponseBodyAsString(), ae.getResponseBodyAsString());
		    	}
			}
    	}
    	catch(Exception e) {
    		waitFor(af);
    		throw e;
    	}
		comp.finish();
	}
	
	
	private static boolean isTextContent(MediaType media){
		
		return Stream.of(
				APPLICATION_JSON, APPLICATION_XML,
				TEXT_PLAIN, TEXT_HTML, TEXT_XML)
				.anyMatch(media::isCompatibleWith);
	}

    private static String excludePaths(String v, HttpRequest hr) {
		if(hr.getExcludePaths() != null) {
			var json = JsonPath.parse(v);
			Stream.of(hr.getExcludePaths()).forEach(json::delete);
	    	v = json.jsonString();
		}
		return v;
    }
    
    private static <T> SafeSupplier<T> execute(CompletableFuture<T> cf){
    	return ()-> {
    		try {
				return cf.get();
			} catch (ExecutionException e) {
				throw e.getCause() instanceof RestClientResponseException 
					? (RestClientResponseException) e.getCause() 
					: e;
			}
    	};
    }

    private static void waitFor(CompletableFuture<?> cf){
		try {
			cf.join();
		}
		catch(Exception ex) {
			log.warn(ex.getMessage());
		}
    }
}
