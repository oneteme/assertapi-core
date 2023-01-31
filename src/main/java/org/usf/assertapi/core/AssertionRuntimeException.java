package org.usf.assertapi.core;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@SuppressWarnings("serial")
public class AssertionRuntimeException extends RuntimeException {
	
	public AssertionRuntimeException(String message) {
		super(message);
	}

	public AssertionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
