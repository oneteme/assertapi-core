package org.usf.assertapi.core;

/**
 * 
 * @author u$f
 * @since
 *
 */
@SuppressWarnings("serial")
public final class AssertionRuntimeException extends RuntimeException {
	
	public AssertionRuntimeException(String msg) {
		super(msg);
	}

	public AssertionRuntimeException(Throwable cause) {
		super("error while testing API", cause);
	}

}
