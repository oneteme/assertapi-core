package org.usf.assertapi.core;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@SuppressWarnings("serial")
public final class ApiAssertionError extends AssertionError {
	
	private final boolean skipped;
	private final Object expected;
	private final Object actual;
	
	ApiAssertionError(boolean skipped, String msg, Object expected, Object actual) {
		super(msg);
		this.skipped = skipped;
		this.expected = expected;
		this.actual = actual;
	}

}
