package fr.enedis.teme.assertapi.core;

import static java.util.Optional.ofNullable;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class DefaultApiAssertions implements ApiAssertions {
	
	private final RestTemplate exTemp;
	private final RestTemplate acTemp;
	private final ResponseComparator comparator;
	
	@Override
	public void assertApi(HttpQuery query) throws Throwable {
		
		var comp = comparator.comparing(query);
		comp.assumeEnabled(query.isEnable());
		
    	String aUrl = query.getActual().uri();
    	CompletableFuture<ResponseEntity<byte[]>> af = query.isParallel() 
    			? supplyAsync(()-> acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().httpMethod()), null, byte[].class), commonPool())
    			: completedFuture(acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().httpMethod()), null, byte[].class));
    	
    	ResponseEntity<byte[]> eRes = null;
    	try {
        	eRes = exTemp.exchange(query.getExpected().uri(), HttpMethod.valueOf(query.getExpected().httpMethod()), null, byte[].class);
    	}
    	catch(RestClientResponseException eExp) {
    		assertApiKO(query, comp, eExp, af);
    	}
    	catch(Exception e) {
    		waitFor(af);
    		comp.assertionFail(e);
    		throw e; //throw it if no exception was thrown
    	}
    	if(eRes != null) {
    		assertApiOK(query, comp, eRes, af);
    	}
		comp.finish();
	}
	
	void assertApiKO(HttpQuery query, ResponseComparator comp, RestClientResponseException eExp, CompletableFuture<ResponseEntity<byte[]>> af) throws Throwable{

		ResponseEntity<byte[]> aRes = null;
		RestClientResponseException aExp = null;
		try {
			aRes = execute(af);
		} catch (RestClientResponseException e) {
			aExp = e;
		}
		catch(Throwable e) {
    		comp.assertionFail(e);
    		throw e; //throw it if no exception was thrown
		}
		if(aRes == null) {
        	comp.assertStatusCode(eExp.getRawStatusCode(), aExp.getRawStatusCode());
        	comp.assertContentType(eExp.getResponseHeaders().getContentType(), aExp.getResponseHeaders().getContentType());
        	var mediaType = eExp.getResponseHeaders().getContentType();
			if(isTextContent(mediaType)) {
	        	if(APPLICATION_JSON.isCompatibleWith(mediaType)) {
		    		comp.assertJsonContent(
							excludePaths(eExp.getResponseBodyAsString(), query.getExpected()),
							excludePaths(aExp.getResponseBodyAsString(), query.getActual()), 
							query.isStrict());
		    	}
		    	else {
		    		comp.assertTextContent(eExp.getResponseBodyAsString(), aExp.getResponseBodyAsString());
		    	}
			}
			else {
				comp.assertByteContent(eExp.getResponseBodyAsByteArray(), aExp.getResponseBodyAsByteArray());
			}
		}
		else {
        	comp.assertStatusCode(eExp.getRawStatusCode(), aRes.getStatusCodeValue()); //KO
		}
	}
	
	private void assertApiOK(HttpQuery query, ResponseComparator comp, ResponseEntity<byte[]> eRes, CompletableFuture<ResponseEntity<byte[]>> af) throws Throwable {

    	ResponseEntity<byte[]> aRes = null;
		try {
			aRes = execute(af);
		} catch (RestClientResponseException aExp) {
        	comp.assertStatusCode(eRes.getStatusCodeValue(), aExp.getRawStatusCode()); //KO
        	throw aExp; //throw it if no exception was thrown
		}
		catch(Exception e) {
    		comp.assertionFail(e);
    		throw e; //throw it if no exception was thrown
		}
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
	
	private static boolean isTextContent(MediaType media){
		
		return media != null && Stream.of(
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
    
    private static <T> T execute(CompletableFuture<T> cf) throws Throwable {
		try {
			return cf.get();
		} catch (ExecutionException e) {
			throw ofNullable(e.getCause()).orElse(e);
		}
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
