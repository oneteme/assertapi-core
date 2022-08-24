package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public final class ApiAssertionsResult {
	
	private final Long id;
	private final ApiExecution expExecution;
	private final ApiExecution actExecution;
	private final TestStatus status;
	private final TestStep step;
	
}
