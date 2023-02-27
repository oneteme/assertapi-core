package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;

import lombok.Getter;
import lombok.ToString;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@ToString
public final class ExecutionConfig {
	
	static final ExecutionConfig DEFAULT_CONFIG = new ExecutionConfig(null, null);

	private boolean enabled;
	private final boolean parallel;
	
	public ExecutionConfig(Boolean enable, Boolean parallel) {
		this.enabled = requireNonNullElse(enable, true);
		this.parallel = requireNonNullElse(parallel, true);
	}
	
	public ExecutionConfig disable() {
		this.enabled = false;
		return this;
	}

	public ExecutionConfig enable() {
		this.enabled = true;
		return this;
	}
}
