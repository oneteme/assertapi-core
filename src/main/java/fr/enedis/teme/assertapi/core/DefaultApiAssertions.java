package fr.enedis.teme.assertapi.core;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
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
	public void assertApi(HttpQuery query)  {
		
	    requireNonNull(query).build();
		var comp = comparator.comparing(query);
		comp.assumeEnabled(query.isEnable());
		
    	var af = submit(query.isParallel(), ()-> exchange(acTemp, query.getActual()));
    	
    	ResponseEntity<byte[]> eRes = null;
    	try {
        	eRes = exchange(exTemp, query.getExpected());
    	}
    	catch(RestClientResponseException eExp) {
    		assertApiKO(query, comp, eExp, af);
    	}
    	catch(Exception e) {
    		try {
    			af.get();
    		}
    		catch(ExecutionException ee) {
    			log.warn(ee.getMessage());
    		}
    		catch (InterruptedException ie) {
    			log.warn("Rest call was interrupted");
    		    Thread.currentThread().interrupt();
    		}
    		comp.assertionFail(e);
    		throw new IllegalStateException(e); // Can't happen : assertionFail should throw exception
    	}
    	if(eRes != null) {
    		assertApiOK(query, comp, eRes, af);
    	}
		comp.finish();
	}
	
	void assertApiKO(HttpQuery query, ResponseComparator comp, RestClientResponseException eExp, Future<ResponseEntity<byte[]>> af) {

		ResponseEntity<byte[]> aRes = null;
		try {
			aRes = execute(af);
			//do not assert here
		} catch (RestClientResponseException aExp) {
        	comp.assertStatusCode(eExp.getRawStatusCode(), aExp.getRawStatusCode());
        	comp.assertContentType(eExp.getResponseHeaders().getContentType(), aExp.getResponseHeaders().getContentType());
        	var mediaType = eExp.getResponseHeaders().getContentType();
			if(isTextContent(mediaType)) {
	        	if(APPLICATION_JSON.isCompatibleWith(mediaType)) {
		    		comp.assertJsonContent(
							excludePaths(eExp.getResponseBodyAsString(), query.getExpected().getOutput()),
							excludePaths(aExp.getResponseBodyAsString(), query.getActual().getOutput()), 
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
		catch(UnexpectedException e) {
    		comp.assertionFail(e.getCause());
    		throw new IllegalStateException(e.getCause()); // Can't happen : assertionFail should throw exception
		}
		catch(Exception e) {
    		comp.assertionFail(e);
    		throw new IllegalStateException(e); // Can't happen : assertionFail should throw exception
		}
		if(aRes != null) {
        	comp.assertStatusCode(eExp.getRawStatusCode(), aRes.getStatusCodeValue()); //KO
		}
	}
	
	private void assertApiOK(HttpQuery query, ResponseComparator comp, ResponseEntity<byte[]> eRes, Future<ResponseEntity<byte[]>> af) {

    	ResponseEntity<byte[]> aRes = null;
		try {
			aRes = execute(af);
		} catch (RestClientResponseException aExp) {
        	comp.assertStatusCode(eRes.getStatusCodeValue(), aExp.getRawStatusCode()); //KO
        	throw aExp; //throw it if no exception was thrown
		}
		catch(UnexpectedException e) {
    		comp.assertionFail(e.getCause());
    		throw new IllegalStateException(e.getCause()); // Can't happen : assertionFail should throw exception
		}
		catch(Exception e) {
    		comp.assertionFail(e);
    		throw new IllegalStateException(e); // Can't happen : assertionFail should throw exception
		}
    	comp.assertStatusCode(eRes.getStatusCodeValue(), aRes.getStatusCodeValue());
    	comp.assertContentType(eRes.getHeaders().getContentType(), aRes.getHeaders().getContentType());
		if(isTextContent(eRes.getHeaders().getContentType())) {
	    	var eCont = decodeResponseBody(eRes.getBody(), query.getExpected().getOutput());
	    	var aCont = decodeResponseBody(aRes.getBody(), query.getActual().getOutput());
	    	if(APPLICATION_JSON.isCompatibleWith(eRes.getHeaders().getContentType())) {
	    		comp.assertJsonContent(
						excludePaths(eCont, query.getExpected().getOutput()),
						excludePaths(aCont, query.getActual().getOutput()), 
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
	
	private static final String decodeResponseBody(byte[] body, RequestOutput out) {
		try {
			return new String(body, out.getCharset());
		} catch (UnsupportedEncodingException e) {
			throw new UnexpectedException(e);
		}
	}
	
	private static boolean isTextContent(MediaType media){
		
		return media != null && Stream.of(
				APPLICATION_JSON, APPLICATION_XML,
				TEXT_PLAIN, TEXT_HTML, TEXT_XML)
				.anyMatch(media::isCompatibleWith);
	}

    private static String excludePaths(String v, RequestOutput out) {
		if(out.getExcludePaths() != null) {
			var json = JsonPath.parse(v);
			Stream.of(out.getExcludePaths()).forEach(json::delete);
	    	v = json.jsonString();
		}
		return v;
    }
    
    private static ResponseEntity<byte[]> execute(Future<ResponseEntity<byte[]>> cf) throws RestClientException, UnexpectedException {
		try {
			return cf.get();
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RestClientException) {
				throw (RestClientException) e.getCause();
			}
			throw new UnexpectedException(ofNullable(e.getCause()).orElse(e));
		}
		catch (InterruptedException e) {
			log.error("Rest call was interrupted", e);
		    Thread.currentThread().interrupt();
			throw new UnexpectedException(e);
		}
    }

    private static ResponseEntity<byte[]> exchange(RestTemplate template, HttpRequest req) throws RestClientException {
    	HttpEntity<String> entity = null;
    	if(req.getBody() != null) {
    		var headers = new HttpHeaders();
    		headers.setContentType(APPLICATION_JSON); // ??
    		entity = new HttpEntity<>(req.getBody(), headers);
    	}
    	return template.exchange(req.getUri(), HttpMethod.valueOf(req.getMethod()), entity, byte[].class);
    }

	private static final <T> Future<T> submit(boolean parallel, Callable<T> callable) {

		return parallel 
				? commonPool().submit(callable)
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
