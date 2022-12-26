package org.usf.assertapi.core;

import static org.usf.assertapi.core.CompareStatus.ERROR;
import static org.usf.assertapi.core.CompareStatus.FAIL;
import static org.usf.assertapi.core.CompareStatus.OK;
import static org.usf.assertapi.core.CompareStage.CONTENT_TYPE;
import static org.usf.assertapi.core.CompareStage.HTTP_CODE;
import static org.usf.assertapi.core.CompareStage.RESPONSE_CONTENT;

import java.util.function.BiConsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 *
 */
@RequiredArgsConstructor
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
public class ResponseComparatorProxy extends ResponseComparator {
	
	private final ResponseComparator comparator;
	private final BiConsumer<ComparableApi, ComparisonResult> tracer;

	private ComparableApi api;
	private ExecutionInfo stableReleaseExec;
	private ExecutionInfo latestReleaseExec;
	
	@Override
	public void prepare(ComparableApi api) {
		this.api = api; //active API
		tryExec(null, ()-> comparator.prepare(api));
	}
	
	@Override
	public void assumeEnabled(boolean enabled) {
		tryExec(null, ()-> comparator.assumeEnabled(enabled));
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
	public void assertJsonContent(String expected, String actual, JsonResponseComparisonConfig config) {
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

	private void tryExec(CompareStage step, Runnable action) {
		try {
			action.run();
		}
		catch(AssertionError e) {
			trace(FAIL, step);
			throw e;
		}
		//other exceptions are catch in ApiAssertion
	}
	
	protected void trace(CompareStatus status, CompareStage step) {
		var res = new ComparisonResult(
				stableReleaseExec,
				latestReleaseExec,
				status, step);
		try {
			tracer.accept(api, res);
		}
		catch(Exception e) {
			log.warn("cannot trace {} => {} : {}", api, res, e.getMessage());
		}
	}
}
