package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;

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
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ClientResponseWrapper.ResponseEntityWrapper;
import org.usf.assertapi.core.ClientResponseWrapper.RestClientResponseExceptionWrapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 *
 */
@RequiredArgsConstructor
public class ApiDefaultAssertion implements ApiAssertion {
	
	private final ResponseComparator comparator;
	private final RestTemplate stableReleaseTemp;
	private final RestTemplate latestReleaseTemp;
	
	private static ExecutorService executor;
	private Future<?> async; //cancel ??
	
	@Override
	public void assertAllAsync(@NonNull Supplier<Stream<ApiRequest>> queries)  {
		this.async = executor().submit(()-> assertAll(queries.get()));
	}
	
	@Override
	public void assertAll(@NonNull Stream<ApiRequest> queries)  {
		queries.forEach(q->{
			try {
				tryAssertOne(q);
			}
	    	catch(Throwable e) {/* do nothing */}
		});
	}
	
	private void tryAssertOne(ApiRequest query) {
		 //assumeEnabled in JUnit does not throw AssertError (should not be catched)
		comparator.assumeEnabled(query);
		try {
			assertOne(query);
		}
		catch (AssertionRuntimeException e) {
			comparator.assertionFail(ofNullable(e.getCause()).orElse(e));
			throw e;
		}
		catch (Exception e) {
			comparator.assertionFail(e);
			throw new AssertionRuntimeException(e);
		}
	}
	
	private void assertOne(ApiRequest query) throws Exception {
		
    	var af = submit(query.executionConfig().isParallel(), ()-> exchange(latestReleaseTemp, query));
    	ResponseEntityWrapper eRes = null;
    	try {
        	eRes = exchange(stableReleaseTemp, query);
        	if(eRes.getStatusCodeValue() != query.getReferStatus()) {
        		throw new AssertionRuntimeException("unexpected stable release response code");
        	}
    	}
    	catch(RestClientResponseExceptionWrapper eExp) {
    		try {
    			var aRes = execute(af);
    			comparator.assertStatusCode(eExp.getStatusCodeValue(), aRes.getStatusCodeValue()); //fail
        		throw illegalStateException(eExp);
    		}
    		catch(RestClientResponseExceptionWrapper aExp) {
        		assertResponseEquals(eExp, aExp, query.getRespConfig());
        		return;
    		}
    	}
    	catch(Exception e) {
    		af.cancel(true); //may throw exception ?
    		throw e;
    	}
		ResponseEntityWrapper aRes = null;
		try {
			aRes = execute(af);
		}
		catch(RestClientResponseExceptionWrapper aExp) {
			comparator.assertStatusCode(eRes.getStatusCodeValue(), aExp.getStatusCodeValue());//fail
    		throw illegalStateException(aExp);
		}
		assertResponseEquals(eRes, aRes, query.getRespConfig());
		comparator.assertOK();
	}
	
	void assertResponseEquals(ClientResponseWrapper expect, ClientResponseWrapper actual, ResponseCompareConfig config) {
		comparator.assertExecution(expect.getRequestExecution(), actual.getRequestExecution());
    	comparator.assertStatusCode(expect.getStatusCodeValue(), actual.getStatusCodeValue());
    	comparator.assertContentType(expect.getContentTypeValue(), actual.getContentTypeValue());
    	var mediaType = expect.getContentType();
		if(isTextCompatible(mediaType)) {
	    	var eCont = expect.getResponseBodyAsString();
	    	var aCont = actual.getResponseBodyAsString();
	    	if(APPLICATION_JSON.isCompatibleWith(mediaType)) {
	    		comparator.assertJsonContent(eCont, aCont, castConfig(config, JsonResponseCompareConfig.class));
	    	}
	    	else {
	    		comparator.assertTextContent(eCont, aCont);
	    	}
		}
		else {
			comparator.assertByteContent(expect.getResponseBodyAsByteArray(), actual.getResponseBodyAsByteArray());
		}
	}
    
    ResponseEntityWrapper exchange(RestTemplate template, ApiRequest req) {
		HttpHeaders headers = null;
		if(req.hasHeaders()) {
			headers = new HttpHeaders();
			for(var e : req.getHeaders().entrySet()) {
				headers.add(e.getKey(), e.getValue());
			}
//			headers.setContentType(APPLICATION_JSON); // ??
		}
		var entity = new HttpEntity<>(req.getBody(), headers);
		var start = currentTimeMillis();
		try {
			var res = template.exchange(req.getUri(), HttpMethod.valueOf(req.getMethod()), entity, byte[].class);
			var exe = new ExecutionInfo(start, currentTimeMillis(), ofNullable(res.getBody()).map(a-> a.length).orElse(0));
			return new ResponseEntityWrapper(res, exe);
		}
		catch(RestClientResponseException e){
			var exe = new ExecutionInfo(start, currentTimeMillis(), e.getResponseBodyAsByteArray().length);
			throw new RestClientResponseExceptionWrapper(e, exe);
		}
    }
	
	static boolean isTextCompatible(MediaType media){
		return media != null && Stream.of(
				APPLICATION_JSON, APPLICATION_XML,
				TEXT_PLAIN, TEXT_HTML, TEXT_XML)
				.anyMatch(media::isCompatibleWith);
	}
	
	private static <T extends ResponseCompareConfig> T castConfig(ResponseCompareConfig obj, Class<T> expectedClass){
		if(expectedClass == null) {
			return null;
		}
		if(expectedClass.isInstance(obj)) {
			return expectedClass.cast(obj);
		}
		throw new AssertionRuntimeException("mismatch API configuration");
	}
    
    private static ResponseEntityWrapper execute(Future<ResponseEntityWrapper> cf) throws RestClientResponseException {
    	Throwable exp = null;
		try {
			return cf.get();
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RestClientResponseException) {
				throw((RestClientResponseException) e.getCause());
			}
			exp = ofNullable(e.getCause()).orElse(e);
		}
		catch (InterruptedException e) {
			currentThread().interrupt();
			exp = e;
		}
		throw new AssertionRuntimeException(exp);
    }
	
	private static ExecutorService executor() {
		if(executor == null) {
			executor = newFixedThreadPool(10); //conf
		}
		return executor;
	}
	

	private static IllegalStateException illegalStateException(Throwable e) {
		return new IllegalStateException("assertion should throw exception", e);
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
						return true; //!important
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
