package org.usf.assertapi.core;

import lombok.Getter;

/**
 * 
 * @author u$f
 *
 */
@Getter
@SuppressWarnings("serial")
public final class ApiAssertionError extends AssertionError {
	
	private final boolean skipped;
	
	ApiAssertionError(boolean skipped, String msg) {
		super(msg);
		this.skipped = skipped;
	}

}
