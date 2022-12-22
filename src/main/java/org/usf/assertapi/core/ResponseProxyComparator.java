package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static org.usf.assertapi.core.TestStatus.ERROR;
import static org.usf.assertapi.core.TestStatus.FAIL;
import static org.usf.assertapi.core.TestStatus.OK;
import static org.usf.assertapi.core.TestStep.CONTENT_TYPE;
import static org.usf.assertapi.core.TestStep.HTTP_CODE;
import static org.usf.assertapi.core.TestStep.RESPONSE_CONTENT;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 *
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseProxyComparator extends ResponseComparator {
	
	private final ResponseComparator comparator;
	private final Consumer<AssertionResult> tracer;
	private final RequestExecution stableRelease;
	private final RequestExecution latestRelease;
	
	private ApiRequest request;
	
	@Override
	public void assumeEnabled(ApiRequest query) {
		this.request = query; //active API
		trace(RESPONSE_CONTENT, ()-> comparator.assumeEnabled(query));
	}
	
	@Override
	public <T> T execute(boolean expected, Supplier<T> c) {
		var o = expected ? stableRelease : latestRelease;
		o.setStart(currentTimeMillis());
		try {
			return comparator.execute(expected, c);
		} finally {
			o.setEnd(currentTimeMillis());
 		}
	}

	@Override
	public void assertStatusCode(int expected, int actual) {
		trace(HTTP_CODE, ()-> comparator.assertStatusCode(expected, actual));
	}

	@Override
	public void assertContentType(String expected, String actual) {
		trace(CONTENT_TYPE, ()-> comparator.assertContentType(expected, actual));
	}

	@Override
	public void assertByteContent(byte[] expected, byte[] actual) {
		trace(RESPONSE_CONTENT, ()-> comparator.assertByteContent(expected, actual));
	}

	@Override
	public void assertTextContent(String expected, String actual) {
		trace(RESPONSE_CONTENT, ()-> comparator.assertTextContent(expected, actual));
	}
	
	@Override
	public void assertJsonContent(String expected, String actual, JsonResponseCompareConfig config) {
		trace(RESPONSE_CONTENT, ()-> comparator.assertJsonContent(expected, actual, config));
	}
	
	@Override
	public void assertOK() { 
		trace(null, ()->{
			comparator.assertOK();
			trace(OK, null);
		});
	}

	@Override
	public void assertionFail(Throwable t) {
		trace(ERROR, null);
		comparator.assertionFail(t);
	}
	

	private void trace(TestStep step, Runnable action) {
		try {
			action.run();
		}
		catch(AssertionError e) {
			trace(FAIL, step);
			throw e;
		}
		catch (Exception e) {
			trace(ERROR, step);
		}
	}
	
	private void trace(TestStatus status, TestStep step) {
		var res = new AssertionResult(
				request.getId(),
				stableRelease,
				latestRelease,
				status,
				step);
		try {
			tracer.accept(res);
		}
		catch(Exception e) {
			log.warn("cannot trace {} : {}", res, e.getMessage());
		}
	}
}
