package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class AssertionResult {
	
	private final Long id;
	private final RequestExecution expExecution;
	private final RequestExecution actExecution;
	private final TestStatus status;
	private final TestStep step;
	
	@Override
	public String toString() {
		return id + " => " + status + (step == null ? "" : step);
	}
	
}
