package org.usf.assertapi.core.exception;

public class TransformerException extends RuntimeException {
    public TransformerException(String msg) {
        super(msg);
    }

    public TransformerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
