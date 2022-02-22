package fr.enedis.teme.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ApiAssertionsResult {
	
	private final String expectedHost;
	private final String actualHost;
	private final ApiRequest query;
	private final TestStatus status;
	private final TestStep step;
	
	public String expectedUrl() {
		return expectedHost + query.getUri();
	}

	public String actualUrl() {
		return actualHost + query.getUri();
	}
	
	@Override
	public String toString() {
		return "TEST " + status + (step == null ? "" : " (Not the same " + step + ")") + " : \n"
				+ "\told : [" + query.getMethod() + "] " + expectedUrl() + "\n" 
				+ "\tnew : [" + query.getMethod() + "] "  + actualUrl();
	}
	
}
