package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.usf.assertapi.core.Utils.sizeOf;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ClientResponseWrapper.HttpRequestWrapper;
import org.usf.assertapi.core.ClientResponseWrapper.ResponseEntityWrapper;
import org.usf.assertapi.core.ClientResponseWrapper.RestClientResponseExceptionWrapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@RequiredArgsConstructor
public final class ApiAssertionExecutor {
	
	private final ResponseComparator comparator;
	private final RestTemplate stableReleaseTemp; //nullable => static response
	private final RestTemplate latestReleaseTemp;
	
	private static ExecutorService executor;
	private Future<?> async; //cancel ??
	
	public void assertAllAsync(@NonNull Supplier<Stream<? extends ComparableApi>> queries)  {
		this.async = executor().submit(()-> assertAll(queries.get()));
	}
	
	public void assertAll(Stream<? extends ComparableApi> stream) {
		stream.forEach(q->{
			try {
				assertApi(q);
			}
	    	catch(Exception | AssertionError e) {/* do nothing exception already logged */}
		});
	}
	
	public void assertApi(@NonNull ComparableApi api) {
		comparator.assertResponse(api, this::execBoth);
	}

	private PairResponse execBoth(ComparableApi api) {
		var af = submit(api.getExecutionConfig().isParallel(), ()-> exchange(latestReleaseTemp, api.latestApi()));
		ClientResponseWrapper expected = null;
    	try {
        	expected = stableReleaseTemp == null 
        			? staticResponse(api.requireStaticResponse())
        			: exchange(stableReleaseTemp, api.stableApi());
        	if(!api.latestApi().acceptStatus(expected.getStatusCodeValue())) {
        		throw new ApiAssertionRuntimeException("unexpected stable release response code");
        	}
    	}
    	catch(Exception e) {
    		af.cancel(true); //may throw exception ?
    		throw e;
    	}
		try {
			return new PairResponse(expected, af.get());
		} catch (InterruptedException e) {
			currentThread().interrupt();
			throw new ApiAssertionRuntimeException("latest release execution was interrupted !", e);
		} catch (ExecutionException e) {
			throw new ApiAssertionRuntimeException("exception during latest release execution !", e);
		}
	}
    
	static ClientResponseWrapper exchange(RestTemplate template, HttpRequest req) {
		HttpHeaders headers = null;
		if(req.hasHeaders()) {
			headers = new HttpHeaders();
			headers.putAll(req.getHeaders());
		}
		var entity = new HttpEntity<>(req.getBody(), headers);
		var method = HttpMethod.valueOf(req.getMethod());
		var start = currentTimeMillis();
		try {
			var res = template.exchange(req.getUri(), method, entity, byte[].class);
			var exe = new ExecutionInfo(start, currentTimeMillis(), res.getStatusCodeValue(), sizeOf(res.getBody()));
			return new ResponseEntityWrapper(res, exe);
		}
		catch(RestClientResponseException e){
			var exe = new ExecutionInfo(start, currentTimeMillis(), e.getRawStatusCode(), sizeOf(e.getResponseBodyAsByteArray()));
			return new RestClientResponseExceptionWrapper(e, exe);
		}
		catch(RestClientException e) {
			throw new ApiAssertionRuntimeException("Unreachable API", e);
		}
    }
	
	static ClientResponseWrapper staticResponse(HttpRequest req) {
		var ms = currentTimeMillis();
		var exe = new ExecutionInfo(ms, ms, req.requireUniqueStatus(), sizeOf(req.getBody()));
		return new HttpRequestWrapper(req, exe);
	}
	
	private static ExecutorService executor() {
		if(executor == null) {
			executor = newFixedThreadPool(10); //conf
		}
		return executor;
	}
	
	private static <T> Future<T> submit(boolean parallel, Callable<T> callable) {
		return parallel 
				? commonPool().submit(callable)
				: new SequentialFuture<>(callable);
	}
	
	@Getter
	@RequiredArgsConstructor
	static class PairResponse {
		
		private final ClientResponseWrapper expected;
		private final ClientResponseWrapper actual;
	}
	
}