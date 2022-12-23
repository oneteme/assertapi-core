package org.usf.assertapi.core;

import static org.usf.assertapi.core.TestStatus.ERROR;
import static org.usf.assertapi.core.TestStatus.FAIL;
import static org.usf.assertapi.core.TestStatus.OK;
import static org.usf.assertapi.core.TestStep.CONTENT_TYPE;
import static org.usf.assertapi.core.TestStep.HTTP_CODE;
import static org.usf.assertapi.core.TestStep.RESPONSE_CONTENT;

import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 *
 */
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
@RequiredArgsConstructor
public class ResponseProxyComparator extends ResponseComparator {
	
	private final ResponseComparator comparator;
	private final Consumer<AssertionResult> tracer;

	private ApiRequest request;
	private ExecutionInfo stableReleaseExec;
	private ExecutionInfo latestReleaseExec;
	
	@Override
	public void assumeEnabled(ApiRequest request) {
		this.request = request; //active API
		tryExec(null, ()-> comparator.assumeEnabled(request));
	}
	
	@Override
	public void assertExecution(ExecutionInfo stableReleaseExec, ExecutionInfo latestReleaseExec) {
		this.stableReleaseExec = stableReleaseExec;
		this.latestReleaseExec = latestReleaseExec;
		tryExec(null, ()-> comparator.assertExecution(stableReleaseExec, latestReleaseExec));
	}

	@Override
	public void assertStatusCode(int expected, int actual) {
		tryExec(HTTP_CODE, ()-> comparator.assertStatusCode(expected, actual));
	}

	@Override
	public void assertContentType(String expected, String actual) {
		tryExec(CONTENT_TYPE, ()-> comparator.assertContentType(expected, actual));
	}

	@Override
	public void assertByteContent(byte[] expected, byte[] actual) {
		tryExec(RESPONSE_CONTENT, ()-> comparator.assertByteContent(expected, actual));
	}

	@Override
	public void assertTextContent(String expected, String actual) {
		tryExec(RESPONSE_CONTENT, ()-> comparator.assertTextContent(expected, actual));
	}
	
	@Override
	public void assertJsonContent(String expected, String actual, JsonResponseCompareConfig config) {
		tryExec(RESPONSE_CONTENT, ()-> comparator.assertJsonContent(expected, actual, config));
	}
	
	@Override
	public void assertOK() { 
		tryExec(null, comparator::assertOK);
		trace(OK, null);
	}

	@Override
	public void assertionFail(Throwable t) {
		trace(ERROR, null);
		comparator.assertionFail(t);
	}
	

	private void tryExec(TestStep step, Runnable action) {
		try {
			action.run();
		}
		catch(AssertionError e) {
			trace(FAIL, step);
			throw e;
		}
		//other exception are catch in ApiAssertion
	}
	
	protected void trace(TestStatus status, TestStep step) {
		var res = new AssertionResult(
				request.getId(),
				stableReleaseExec,
				latestReleaseExec,
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
