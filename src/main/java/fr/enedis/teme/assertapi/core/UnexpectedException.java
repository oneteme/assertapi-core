package fr.enedis.teme.assertapi.core;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("serial")
public final class UnexpectedException extends RuntimeException {

	public UnexpectedException(Throwable cause) {
		super(requireNonNull(cause));
	}
}
