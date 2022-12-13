package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;

import lombok.Getter;

//TODO rename to ExecutionConfig
@Getter
public final class AssertionConfig {

	@Deprecated(forRemoval = true)
	private final boolean debug;
	private final boolean enable;
	@Deprecated(forRemoval = true)
	private final boolean strict;
	private final boolean parallel;
	@Deprecated(forRemoval = true)
	private final String[] excludePaths; //only JSON
	
	public AssertionConfig(Boolean debug, Boolean enable, Boolean strict, Boolean parallel, String[] excludePaths) {
		this.debug = ofNullable(debug).orElse(false);
		this.enable = ofNullable(enable).orElse(true);
		this.strict = ofNullable(strict).orElse(true);
		this.parallel = ofNullable(parallel).orElse(true);
		this.excludePaths = excludePaths;
	}
	
	static AssertionConfig defaultConfig() {
		return new AssertionConfig(null, null, null, null, null);
	}

}
