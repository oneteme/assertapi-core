package org.usf.assertapi.core;

import java.util.Map;

import lombok.Getter;

@Getter
public class ApiNonRegression extends ApiRequest implements Api {

	private final String description; //request description
	private final ResponseCompareConfig respConfig; //nullable
	
	public ApiNonRegression(Long id, String uri, String method, Map<String, String> headers,
			String name, Integer version, String description, int[] referStatus, ExecutionConfig execConfig, ResponseCompareConfig respConfig) {
		
		super(id, uri, method, headers, name, version, referStatus, execConfig);
		this.description = description;
		this.respConfig = respConfig;
	}

	@Override
	public ApiRequest stableApi() {
		return this;
	}

	@Override
	public ApiRequest latestApi() {
		return this;
	}
}
