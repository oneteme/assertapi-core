package org.usf.assertapi.core;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@SuppressWarnings("serial")
public final class ApiAssertionRuntimeException extends RuntimeException {
	
	public ApiAssertionRuntimeException(String msg) {
		super(msg);
	}

	public ApiAssertionRuntimeException(Throwable cause) {
		super("error while testing API", cause);
	}

}
