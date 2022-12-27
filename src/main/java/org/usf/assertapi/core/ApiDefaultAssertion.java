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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
	
	@Override
	public void assertAll(@NonNull Stream<? extends ComparableApi> queries)  {
		queries.forEach(q->{
			try {
				assertApi(q);
			}
	    	catch(Throwable e) {/* do nothing */} //Exception + Error
		});
	}
	
	public void assertApi(ComparableApi api) {
		tryExec(()-> comparator.prepare(api));
		//assumeEnabled in JUnit does not throw AssertError (should not be catched)
		comparator.assumeEnabled(api.getExecutionConfig().isEnabled());
		tryExec(()-> assertOne(api));
	}
	
	void tryExec(SafeRunnable action){
		try {
			action.run();
		}
		catch (ApiAssertionRuntimeException e) {
			comparator.assertionFail(ofNullable(e.getCause()).orElse(e));
			throw e;
		}
		catch (Exception e) {
			comparator.assertionFail(e);
			throw new ApiAssertionRuntimeException(e);
		}
	}
	
	private void assertOne(ComparableApi api) throws Exception {
		
    	var af = submit(api.getExecutionConfig().isParallel(), 
				()-> exchange(latestReleaseTemp, api.latestApi()));
    	ResponseEntityWrapper eRes = null;
    	try {
        	eRes = exchange(stableReleaseTemp, api.stableApi());
        	if(!api.stableApi().acceptStatus(eRes.getStatusCodeValue())) {
        		throw new ApiAssertionRuntimeException("unexpected stable release response code");
        	}
    	}
    	catch(RestClientResponseExceptionWrapper eExp) {
        	if(!api.stableApi().acceptStatus(eExp.getStatusCodeValue())) {
        		throw new ApiAssertionRuntimeException("unexpected stable release response code");
        	}
    		try {
    			var aRes = execute(af);
    			comparator.assertStatusCode(eExp.getStatusCodeValue(), aRes.getStatusCodeValue()); //fail
        		throw illegalStateException(eExp);
    		}
    		catch(RestClientResponseExceptionWrapper aExp) {
    			comparator.assertResponse(eExp, aExp, api.getComparisonConfig());
    		}
    	}
    	catch(Exception e) {
    		af.cancel(true); //may throw exception ?
    		throw e;
    	}
    	if(eRes != null) {
			ResponseEntityWrapper aRes = null;
			try {
				aRes = execute(af);
			}
			catch(RestClientResponseExceptionWrapper aExp) {
				comparator.assertStatusCode(eRes.getStatusCodeValue(), aExp.getStatusCodeValue());//fail
	    		throw illegalStateException(aExp);
			}
			comparator.assertResponse(eRes, aRes, api.getComparisonConfig());
    	}
    	comparator.assertOK();
	}
    
    ResponseEntityWrapper exchange(RestTemplate template, HttpRequest req) {
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
		throw new ApiAssertionRuntimeException(exp);
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

	@FunctionalInterface
	interface SafeRunnable {

		void run() throws Exception;
	}
	
}
