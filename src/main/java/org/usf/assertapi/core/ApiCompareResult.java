package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 *
 */
@Getter
@RequiredArgsConstructor
public final class ApiCompareResult {
	
	private final ExecutionInfo stableReleaseExecution;
	private final ExecutionInfo latestReleaseExecution;
	private final TestStatus status;
	private final TestStep step;
	
	@Override
	public String toString() {
		return status + (step == null ? "" : "@" + step) +
				"\n\t\t stable : " + stableReleaseExecution +
				"\n\t\t latest : " + latestReleaseExecution;
	}
	
}
