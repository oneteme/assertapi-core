package fr.enedis.teme.assertapi.core;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
		
	    requireNonNull(query).build();
		var comp = comparator.comparing(query);
		comp.assumeEnabled(query.isEnable());
		
    	String aUrl = query.getActual().getUri();
    	var af = submit(query.isParallel(), ()-> acTemp.exchange(aUrl, HttpMethod.valueOf(query.getActual().getMethod()), entity(query.getActual()), byte[].class));
    	
    	ResponseEntity<byte[]> eRes = null;
    	try {
        	eRes = exTemp.exchange(query.getExpected().getUri(), HttpMethod.valueOf(query.getExpected().getMethod()), entity(query.getExpected()), byte[].class);
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
	
	void assertApiKO(HttpQuery query, ResponseComparator comp, RestClientResponseException eExp, Future<ResponseEntity<byte[]>> af) throws Throwable{

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
		if(aExp != null) {
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
	
	private void assertApiOK(HttpQuery query, ResponseComparator comp, ResponseEntity<byte[]> eRes, Future<ResponseEntity<byte[]>> af) throws Throwable {

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
	    	var eCont = new String(eRes.getBody(), query.getExpected().getOutput().getCharset());
	    	var aCont = new String(aRes.getBody(), query.getActual().getOutput().getCharset());
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
		if(hr.getOutput().getExcludePaths() != null) {
			var json = JsonPath.parse(v);
			Stream.of(hr.getOutput().getExcludePaths()).forEach(json::delete);
	    	v = json.jsonString();
		}
		return v;
    }
    
    private static <T> T execute(Future<T> cf) throws Throwable {
		try {
			return cf.get();
		} catch (ExecutionException e) {
			throw ofNullable(e.getCause()).orElse(e);
		}
    }

    private static void waitFor(Future<?> cf){
		try {
			cf.get();
		}
		catch(Exception ex) {
			log.warn(ex.getMessage());
		}
    }
    
    private static HttpEntity<String> entity(HttpRequest req){
    	if(req.getBody() == null) {
    		return null;
    	}
    	var headers = new HttpHeaders();
    	headers.setContentType(APPLICATION_JSON);
    	return new HttpEntity<>(req.getBody(), headers);
    }

	private static final <T> Future<T> submit(boolean parallel, Callable<T> callable) {

		return parallel 
				? ForkJoinPool.commonPool().submit(callable)
				: new Future<>() {
					@Override
					public T get() throws InterruptedException, ExecutionException {
						try {
							return callable.call();
						} catch (Exception e) {
							throw new ExecutionException(e);
						}
					}

					@Override
					public boolean cancel(boolean mayInterruptIfRunning) {
						throw new UnsupportedOperationException();
					}

					@Override
					public boolean isCancelled() {
						throw new UnsupportedOperationException();
					}

					@Override
					public boolean isDone() {
						throw new UnsupportedOperationException();
					}

					@Override
					public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
						throw new UnsupportedOperationException();
					}
				};
	}
}
