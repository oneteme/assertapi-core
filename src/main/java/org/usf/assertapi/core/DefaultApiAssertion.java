package org.usf.assertapi.core;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;
import static org.usf.assertapi.core.ResponseComparator.expectException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultApiAssertion implements ApiAssertion {
	
	private final ResponseComparator comparator;
	private final RestTemplate stableReleaseTemp;
	private final RestTemplate latestReleaseTemp;
	
	private static ExecutorService executor;
	private Future<?> async; //cancel ??
	
	interface Execution {
		
		List<ApiRequest> get();
		
		void onInit();
		
		void onComplete();
		
	}
	
	private static ExecutorService executor() {
		if(executor == null) {
			executor = newFixedThreadPool(10); //conf
		}
		return executor;
	}

	public void execAsync(@NonNull List<ApiRequest> queries)  {
		execAsync(()-> queries);
	}

	@Override
	public void execAsync(@NonNull Supplier<List<ApiRequest>> queries)  {
		this.async = executor().submit(()-> assertApi(queries.get()));
	}

	public void assertApi(@NonNull List<ApiRequest> queries)  {
		for(var q : queries) {
			try {
				exec(q);
			}
	    	catch(Throwable e) {/* do nothing */}
		}
	}
	
	@Override
	public void exec(@NonNull ApiRequest query) {
		comparator.assumeEnabled(query);
		
    	var af = submit(query.getConfiguration().isParallel(), ()-> exchange(latestReleaseTemp, query, comparator));
    	ResponseEntity<byte[]> eRes = null;
    	try {
        	eRes = exchange(stableReleaseTemp, query, comparator);
        	if(eRes.getStatusCodeValue() != query.getReferStatus()) {
        		throw new ReferInvalidResponseException(query.getReferStatus(), eRes.getStatusCodeValue());
        	}
    	}
    	catch(RestClientResponseException eExp) {
    		try {
    			var aRes = execute(af, comparator);
    			comparator.assertStatusCode(eExp.getRawStatusCode(), aRes.getStatusCodeValue()); //fail
        		throw expectException(eExp);
    		}
    		catch(RestClientResponseException aExp) { 
        		assertApiKO(query, eExp, aExp);
    		}
    	}
    	catch(Exception e) {
    		af.cancel(true); //may throw exception ?
    		comparator.assertionFail(e);//error
    		throw expectException(e);
    	}
    	if(eRes != null) {
    		ResponseEntity<byte[]> aRes = null;
    		try {
    			aRes = execute(af, comparator);
    		}
    		catch(RestClientResponseException aExp) {
    			comparator.assertStatusCode(eRes.getStatusCodeValue(), aExp.getRawStatusCode());//fail
        		throw expectException(aExp);
    		}
    		assertApiOK(query, eRes, aRes);
    	}
		comparator.assertOK();
	}
	
	void assertApiKO(ApiRequest query, RestClientResponseException eExp, RestClientResponseException aExp) {
    	comparator.assertStatusCode(eExp.getRawStatusCode(), aExp.getRawStatusCode());
    	comparator.assertContentType(eExp.getResponseHeaders().getContentType(), aExp.getResponseHeaders().getContentType());
    	var mediaType = eExp.getResponseHeaders().getContentType();
		if(isTextContent(mediaType)) {
        	if(APPLICATION_JSON.isCompatibleWith(mediaType)) {
	    		comparator.assertJsonContent(
						excludePaths(eExp.getResponseBodyAsString(), query.getConfiguration()),
						excludePaths(aExp.getResponseBodyAsString(), query.getConfiguration()), 
						query.getConfiguration().isStrict());
	    	}
	    	else {
	    		comparator.assertTextContent(eExp.getResponseBodyAsString(), aExp.getResponseBodyAsString());
	    	}
		}
		else {
			comparator.assertByteContent(eExp.getResponseBodyAsByteArray(), aExp.getResponseBodyAsByteArray());
		}
	}
	
	private void assertApiOK(ApiRequest query, ResponseEntity<byte[]> eRes, ResponseEntity<byte[]> aRes) {
    	comparator.assertStatusCode(eRes.getStatusCodeValue(), aRes.getStatusCodeValue());
    	comparator.assertContentType(eRes.getHeaders().getContentType(), aRes.getHeaders().getContentType());
    	var mediaType = eRes.getHeaders().getContentType();
		if(isTextContent(mediaType)) {
	    	var eCont = new String(eRes.getBody(), UTF_8);
	    	var aCont = new String(aRes.getBody(), UTF_8);
	    	if(APPLICATION_JSON.isCompatibleWith(eRes.getHeaders().getContentType())) {
	    		comparator.assertJsonContent(
						excludePaths(eCont, query.getConfiguration()),
						excludePaths(aCont, query.getConfiguration()), 
						query.getConfiguration().isStrict());
	    	}
	    	else {
	    		comparator.assertTextContent(eCont, aCont);
	    	}
		}
		else {
			comparator.assertByteContent(eRes.getBody(), aRes.getBody());
		}
	}
	
	private static boolean isTextContent(MediaType media){
		return media != null && Stream.of(
				APPLICATION_JSON, APPLICATION_XML,
				TEXT_PLAIN, TEXT_HTML, TEXT_XML)
				.anyMatch(media::isCompatibleWith);
	}

    private static String excludePaths(String v, AssertionConfig out) {
		if(out.getExcludePaths() != null) {
			var json = JsonPath.parse(v);
			Stream.of(out.getExcludePaths()).forEach(json::delete);
	    	v = json.jsonString();
		}
		return v;
    }
    
    private static ResponseEntity<byte[]> execute(Future<ResponseEntity<byte[]>> cf, ResponseComparator comp) throws RestClientResponseException {
    	Throwable exp = null;
		try {
			return cf.get();
		} catch (ExecutionException e) {
			//TODO check RestClientException
			if(e.getCause() instanceof RestClientResponseException) {
				throw((RestClientResponseException) e.getCause());
			}
			exp = ofNullable(e.getCause()).orElse(e);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			exp = e;
		}
		comp.assertionFail(exp);
		throw expectException(exp);
    }
    
    private ResponseEntity<byte[]> exchange(RestTemplate template, ApiRequest req, ResponseComparator cmp) {
		HttpHeaders headers = null;
		if(req.hasHeaders()) {
			headers = new HttpHeaders();
			for(var e : req.getHeaders().entrySet()) {
				headers.add(e.getKey(), e.getValue());
			}
			headers.setContentType(APPLICATION_JSON); // ??
		}
		var entity = new HttpEntity<>(req.getBody(), headers);
    	return cmp.execute(template == stableReleaseTemp, 
    			()-> template.exchange(req.getUri(), HttpMethod.valueOf(req.getMethod()), entity, byte[].class));
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
							throw new ExecutionException(e); //manage exceptions
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
