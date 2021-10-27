package fr.enedis.teme.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ApiAssertionsResult {
	
	private final String expectedHost;
	private final String actualHost;
	private final HttpQuery query;
	private final TestStatus status;
	
	@Override
	public String toString() {
		return "TEST " + status  + " : \n" 
				+ "\told : [" + query.getExpected().getMethod() + "] " + expectedHost + query.getExpected().getUri() + "\n" 
				+ "\tnew : [" + query.getActual().getMethod() + "] "  + actualHost + query.getActual().getUri();
	}
	
	public enum TestStatus {
		
		OK, KO, SKIP;
	}

}
