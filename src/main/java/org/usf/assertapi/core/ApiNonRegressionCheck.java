package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@JsonInclude(NON_NULL)
public final class ApiNonRegressionCheck extends ApiRequest implements ApiCheck {

	private final String description; //request description
	private final ResponseCompareConfig respConfig; //nullable
	
	public ApiNonRegressionCheck(Long id, String name, Integer version, 
			String uri, String method, Map<String, String> headers,
			int[] acceptableStatus, ExecutionConfig execConfig, String description, ResponseCompareConfig respConfig) {
		
		super(id, name, version, uri, method, headers, acceptableStatus, execConfig);
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
