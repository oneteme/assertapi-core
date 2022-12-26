package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class ExecutionConfig {

	private boolean enabled;
	private final boolean parallel;
	
	public ExecutionConfig(Boolean enable, Boolean parallel) {
		this.enabled = ofNullable(enable).orElse(true);
		this.parallel = ofNullable(parallel).orElse(true);
	}
	public ExecutionConfig disable() {
		this.enabled = false;
		return this;
	}

	public ExecutionConfig enable() {
		this.enabled = true;
		return this;
	}
	
	static ExecutionConfig defaultConfig() {
		return new ExecutionConfig(null, null);
	}

}
