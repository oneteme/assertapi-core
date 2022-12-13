package org.usf.assertapi.core;
import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public final class ReferInvalidResponseException extends Exception {

	private final int expectedCode;
	private final int actualCode;

	public ReferInvalidResponseException(int expectedCode, int actualCode) {
		super("");
		this.expectedCode = expectedCode;
		this.actualCode = actualCode;
	}
	
}
