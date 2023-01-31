package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@RequiredArgsConstructor
public final class ComparisonResult {
	
	private final ExecutionInfo stableApiExecution;
	private final ExecutionInfo latestApiExecution;
	private final ComparisonStatus status;
	private final ComparisonStage step;
	
	@Override
	public String toString() {
		return status + (step == null ? "" : "@" + step) +
				"\n\t\t stable : " + stableApiExecution +
				"\n\t\t latest : " + latestApiExecution;
	}
	
}
