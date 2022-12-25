package org.usf.assertapi.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Api {
	
	Long getId();

	ApiRequest stableApi();

	ApiRequest latestApi();

	ResponseCompareConfig getRespConfig();

	@JsonIgnore
	default boolean isEnabled() {
		return stableApi().getExecConfig().isEnable() 
				&& latestApi().getExecConfig().isEnable();
	}
	
	@JsonIgnore
	default boolean isParallel() {
		return stableApi().getExecConfig().isParallel() 
				&& latestApi().getExecConfig().isParallel();
	}
}
