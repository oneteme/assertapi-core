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
public final class AssertionResult {
	
	private final Long id;
	private final ExecutionInfo expExecution;
	private final ExecutionInfo actExecution;
	private final TestStatus status;
	private final TestStep step;
	
	@Override
	public String toString() {
		return id + " => " + status + (step == null ? "" : "@" + step);
	}
	
}
