package org.usf.assertapi.core;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@SuppressWarnings("serial")
public class ApiAssertionRuntimeException extends RuntimeException {
	
	public ApiAssertionRuntimeException(String message) {
		super(message);
	}

	public ApiAssertionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
