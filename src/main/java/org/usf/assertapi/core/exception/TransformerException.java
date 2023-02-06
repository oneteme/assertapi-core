package org.usf.assertapi.core.exception;

import org.usf.assertapi.core.ApiAssertionRuntimeException;

@Deprecated(forRemoval = true)
@SuppressWarnings("serial")
public final class TransformerException extends ApiAssertionRuntimeException {

	public TransformerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransformerException(String message) {
		super(message);
	}
}
