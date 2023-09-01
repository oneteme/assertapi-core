package org.usf.assertapi.core;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.usf.assertapi.core.ApiExecutor.PairResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @author u$f
 *
 */
@RequiredArgsConstructor
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
public class ResponseComparatorProxy extends ResponseComparator {
	
	private final ResponseComparator comparator;
	private final BiConsumer<ApiRequest, ComparisonResult> tracer;

	private ExecutionInfo stableReleaseExec;
	private ExecutionInfo latestReleaseExec;
	
	
	@Override
	void setExecutor(ApiExecutor executor) {
		comparator.setExecutor(executor);
	}
	
	@Override
	public void assumeEnabled(boolean enabled) {
		comparator.assumeEnabled(enabled);
	}

	@Override
	protected PairResponse execute(ApiRequest api) {
		var res = comparator.execute(api);
		this.stableReleaseExec = res.getExpected().getRequestExecution();
		this.latestReleaseExec = res.getActual().getRequestExecution();
		return res;
	}
	
	@Override
	public void assertElapsedTime(ExecutionInfo stableReleaseExec, ExecutionInfo latestReleaseExec) {
		comparator.assertElapsedTime(stableReleaseExec, latestReleaseExec);
	}
	
	@Override
	public void assertStatusCode(int expected, int actual) {
		comparator.assertStatusCode(expected, actual);
	}

	@Override
	public void assertContentType(String expected, String actual) {
		comparator.assertContentType(expected, actual);
	}

	@Override
	public void assertHeaders(Map<String, List<String>> expected, Map<String, List<String>> actual) {
		comparator.assertHeaders(expected, actual);
	}

	@Override
	public void assertByteContent(byte[] expected, byte[] actual, ModelComparator<?> config) {
		comparator.assertByteContent(expected, actual, config);
	}

	@Override
	public void assertTextContent(String expected, String actual) {
		comparator.assertTextContent(expected, actual);
	}

	@Override
	public void assertJsonContent(String expected, String actual, ModelComparator<?> config) {
		comparator.assertJsonContent(expected, actual, config);
	}

	@Override
	public void assertionFail(AssertionError err) {
		comparator.assertionFail(err);
	}
		
	@Override
	public void finish(ApiRequest api, ComparisonStatus status) {
		var result = new ComparisonResult(
				stableReleaseExec,
				latestReleaseExec,
				status, getCurrentStage());
		try {
			tracer.accept(api, result);
		}
		catch(Exception e) {
			log.warn("cannot trace {} => {} : {}", api, result, e.getMessage());
		}
		comparator.finish(api, status);
	}
	
	@Override
	public ComparisonStage getCurrentStage() {
		return comparator.getCurrentStage();
	}
	
}
