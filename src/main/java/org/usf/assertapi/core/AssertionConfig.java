package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;

import lombok.Getter;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
public final class AssertionConfig {

	private final boolean debug;
	private boolean enable;
	private final boolean strict;
	private final boolean parallel;
	private final String[] excludePaths;
	
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
