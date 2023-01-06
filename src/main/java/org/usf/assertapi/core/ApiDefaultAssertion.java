package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.ForkJoinPool.commonPool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ClientResponseWrapper.ResponseEntityWrapper;
import org.usf.assertapi.core.ClientResponseWrapper.RestClientResponseExceptionWrapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
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
	public void assertAllAsync(@NonNull Supplier<Stream<? extends ComparableApi>> queries)  {
		this.async = executor().submit(()-> assertAll(queries.get()));
	}
	
	public void assertApi(ComparableApi api) {
		try {
			assertOne(api);
		}
		catch (Exception | AssertionError e) {
			comparator.assertionFail(e);
		}
	}
	
	private void assertOne(ComparableApi api) throws Exception {
		comparator.prepare(api);
		comparator.assumeEnabled(api.getExecutionConfig().isEnabled());

		ClientResponseWrapper expected = null;
		ClientResponseWrapper actual = null;
		
		var af = submit(api.getExecutionConfig().isParallel(), 
				()-> exchange(latestReleaseTemp, api.latestApi()));
    	try {
        	expected = exchange(stableReleaseTemp, api.stableApi());
    	}
    	catch(RestClientResponseExceptionWrapper e) {
    		expected = e;
    	}
    	catch(Exception e) {
    		af.cancel(true); //may throw exception ?
    		throw e;
    	}
    	if(!api.stableApi().acceptStatus(expected.getStatusCodeValue())) {
    		af.cancel(true); //may throw exception ?
    		throw new ApiAssertionRuntimeException("unexpected stable release response code");
    	}
		try {
			actual = execute(af);
		}
		catch(RestClientResponseExceptionWrapper e) {
			actual = e;
		}
		comparator.assertResponse(expected, actual, api.getContentComparator());
	}
    
    ResponseEntityWrapper exchange(RestTemplate template, HttpRequest req) throws RestClientResponseExceptionWrapper {
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
			var exe = new ExecutionInfo(start, currentTimeMillis(), res.getStatusCodeValue(), ofNullable(res.getBody()).map(a-> a.length).orElse(0));
			return new ResponseEntityWrapper(res, exe);
		}
		catch(RestClientResponseException e){
			var exe = new ExecutionInfo(start, currentTimeMillis(), e.getRawStatusCode(), e.getResponseBodyAsByteArray().length);
			throw new RestClientResponseExceptionWrapper(e, exe);
		}
    }
	
    private static ResponseEntityWrapper execute(Future<ResponseEntityWrapper> cf) throws RestClientResponseExceptionWrapper, ExecutionException, InterruptedException {
		try {
			return cf.get();
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RestClientResponseExceptionWrapper) {
				throw((RestClientResponseExceptionWrapper) e.getCause());
			}
			throw e;
		}
		catch (InterruptedException e) {
			currentThread().interrupt();
			throw e;
		}
    }
	
	private static ExecutorService executor() {
		if(executor == null) {
			executor = newFixedThreadPool(10); //conf
		}
		return executor;
	}
	
	private static final <T> Future<T> submit(boolean parallel, Callable<T> callable) {
		return parallel 
				? commonPool().submit(callable)
				: new SequentialFuture<>(callable);
	}
}
