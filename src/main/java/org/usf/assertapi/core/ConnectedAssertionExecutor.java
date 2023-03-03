package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.nio.file.Files.readAllBytes;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.usf.assertapi.core.Utils.defaultMapper;
import static org.usf.assertapi.core.Utils.isEmpty;
import static org.usf.assertapi.core.Utils.notImplemented;
import static org.usf.assertapi.core.Utils.sizeOf;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ClientResponseWrapper.ResponseEntityWrapper;
import org.usf.assertapi.core.ClientResponseWrapper.RestClientResponseExceptionWrapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@RequiredArgsConstructor
public class ConnectedAssertionExecutor implements ApiExecutor {
	
	private static final ObjectMapper mapper = defaultMapper();
	
	private final RestTemplate stableApiTemplate; //nullable => static response
	private final RestTemplate latestApiTemplate;

	public PairResponse exchange(ApiRequest api) {
		var af = runLatest(api);
		ClientResponseWrapper expected = null;
    	try {
        	expected = runStable(api);
        	if(!api.accept(expected.getStatusCodeValue())) {
        		throw new ApiAssertionRuntimeException("unexpected stable release response code : " + expected.getStatusCodeValue());
        	}
    	}
    	catch(Exception e) {
    		af.cancel(true); //may throw exception ?
    		throw e;
    	}
		try {
			loadComparators(api);
			return new PairResponse(expected, af.get());
		} catch (InterruptedException e) {
			currentThread().interrupt();
			throw new ApiAssertionRuntimeException("latest release execution was interrupted !", e);
		} catch (ExecutionException e) {
			throw new ApiAssertionRuntimeException("exception during latest release execution !", e);
		}
	}
	
	ClientResponseWrapper runStable(ApiRequest api) {
		return exchange(api.stable(), stableApiTemplate);
	}
	
	Future<ClientResponseWrapper> runLatest(ApiRequest api) {
		return submit(api.getExecution().isParallel(), ()-> exchange(api.latest(), latestApiTemplate));
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
	
	static byte[] loadBody(HttpRequest req) {
		if(req.getBody() != null || req.getLazyBody() == null) {
			return req.getBody();
		}
		if(req.getLazyBody().matches("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")) { //get reference from server
			throw notImplemented();
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
	
	static void loadComparators(ApiRequest req) {
		if(isEmpty(req.getComparators()) && req.getLazyComparators() != null) {
			if(req.getLazyComparators().matches("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")) { //get reference from server
				throw notImplemented();
			}
			else {
				var f = new File(requireNonNull(req.getLocation()).resolve(req.getLazyComparators()));
				if(!f.exists()) {
					throw new NoSuchElementException("file not found : " + f.toURI());
				}
				try {
					req.setComparators(mapper.readValue(f, new TypeReference<Map<String, ModelComparator<?>>>() {}));
				} catch (IOException e) {
					throw new ApiAssertionRuntimeException("cannot read file : " + f, e);
				}
			}
		}
	}	
	
	private static <T> Future<T> submit(boolean parallel, Callable<T> callable) {
		return parallel 
				? commonPool().submit(callable)
				: new SequentialFuture<>(callable);
	}
}