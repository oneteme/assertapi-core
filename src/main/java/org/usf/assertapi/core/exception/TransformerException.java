package org.usf.assertapi.core.exception;

import org.usf.assertapi.core.AssertionRuntimeException;

@Deprecated(forRemoval = true)
@SuppressWarnings("serial")
public final class TransformerException extends AssertionRuntimeException {

	public TransformerException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransformerException(String message) {
		super(message);
	}
}
