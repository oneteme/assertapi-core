package org.usf.assertapi.core;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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

	private ApiRequest currentApi;
	private ExecutionInfo stableReleaseExec;
	private ExecutionInfo latestReleaseExec;
	
	@Override
	void setExecutor(ApiExecutor executor) {
		comparator.setExecutor(executor);
	}
	
	@Override
	public void before(ApiRequest api) {
		this.currentApi = api; //current API
		comparator.before(api);
	}
	
	@Override
	public void assertElapsedTime(ExecutionInfo stableReleaseExec, ExecutionInfo latestReleaseExec) {
		this.stableReleaseExec = stableReleaseExec;
		this.latestReleaseExec = latestReleaseExec;
		comparator.assertElapsedTime(stableReleaseExec, latestReleaseExec);
	}

	@Override
	public void assumeEnabled(boolean enabled) {
		comparator.assumeEnabled(enabled);
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
	public void assertByteContent(byte[] expected, byte[] actual) {
		comparator.assertByteContent(expected, actual);
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
	public void assertionFail(Throwable t) {
		comparator.assertionFail(t);
	}
		
	@Override
	public void finish(ComparisonStatus status) {
		var result = new ComparisonResult(
				stableReleaseExec,
				latestReleaseExec,
				status, getCurrentStage());
		try {
			tracer.accept(currentApi, result);
		}
		catch(Exception e) {
			log.warn("cannot trace {} => {} : {}", currentApi, result, e.getMessage());
		}
		comparator.finish(status);
	}
	
	@Override
	public ComparisonStage getCurrentStage() {
		return comparator.getCurrentStage();
	}
	
}
