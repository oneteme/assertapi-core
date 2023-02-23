package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.nio.file.Files.readAllBytes;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.usf.assertapi.core.Utils.isEmpty;
import static org.usf.assertapi.core.Utils.sizeOf;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
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
	
	public void assertAllAsync(@NonNull Supplier<Stream<ApiRequest>> queries)  {
		this.async = executor().submit(()-> assertAll(queries.get()));
	}
	
	public void assertAll(Stream<ApiRequest> stream) {
		stream.forEach(q->{
			try {
				assertApi(q);
			}
	    	catch(Exception | AssertionError e) {/* do nothing exception already logged */}
		});
	}
	
	public void assertApi(@NonNull ApiRequest api) {
		comparator.assertResponse(api, this::execBoth);
	}

	private PairResponse execBoth(ApiRequest api) {
		var af = submit(api.getExecutionConfig().isParallel(), ()-> exchange(api.latestApi(), latestReleaseTemp));
		ClientResponseWrapper expected = null;
    	try {
        	expected = stableReleaseTemp == null 
        			? staticResponse(api.staticResponse())
        			: exchange(api.stableApi(), stableReleaseTemp);
        	if(!api.acceptStatus(expected.getStatusCodeValue())) {
        		throw new ApiAssertionRuntimeException("unexpected stable release response code : " + expected.getStatusCodeValue());
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
    
	static ClientResponseWrapper exchange(HttpRequest req, RestTemplate template) {
		HttpHeaders headers = null;
		if(!isEmpty(req.getHeaders())) {
			headers = new HttpHeaders();
			headers.putAll(req.getHeaders());
		}
		var entity = new HttpEntity<>(loadBody(req), headers);
		var method = HttpMethod.valueOf(req.getMethod());
		var start = currentTimeMillis();
		try {
			var uri = req.getUri().startsWith("/") ? req.getUri() : "/" + req.getUri();
			var res = template.exchange(uri, method, entity, byte[].class);
			var exe = new ExecutionInfo(start, currentTimeMillis(), res.getStatusCodeValue(), sizeOf(res.getBody()));
			return new ResponseEntityWrapper(res, exe);
		}
		catch(RestClientResponseException e){
			var exe = new ExecutionInfo(start, currentTimeMillis(), e.getRawStatusCode(), sizeOf(e.getResponseBodyAsByteArray()));
			return new RestClientResponseExceptionWrapper(e, exe);
		}
		catch(RestClientException e) {
			throw new ApiAssertionRuntimeException("unreachable API", e);
		}
    }
	
	static ClientResponseWrapper staticResponse(StaticResponse res) {
		if(res == null) {
			throw new Utils.EmptyValueException("ApiRequest", "staticResponse");
		}
		var ms = currentTimeMillis();
		var body = loadBody(res);
		if(body != res.getBody()) {
			res = res.withBody(body);
		}
		var exe = new ExecutionInfo(ms, currentTimeMillis(), res.getStatus(), sizeOf(res.getBody()));
		return new HttpRequestWrapper(res, exe);
	}
	
	private static byte[] loadBody(HttpRequest req) {
		if(req.getBody() != null || req.getLazyBody() == null) {
			return req.getBody();
		}
		if(req.getLazyBody().matches("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")) { //get reference from server
			throw new UnsupportedOperationException("not yet implemented");
		}
		else {
			var f = new File(requireNonNull(req.getLocation()).resolve(req.getLazyBody()));
			if(!f.exists()) {
				throw new NoSuchElementException("file not found : " + f.toURI());
			}
			try {
				return readAllBytes(f.toPath());
			} catch (IOException e) {
				throw new ApiAssertionRuntimeException("cannot read file : " + f, e);
			}
		}
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
	static final class PairResponse {
		
		private final ClientResponseWrapper expected;
		private final ClientResponseWrapper actual;
	}
}