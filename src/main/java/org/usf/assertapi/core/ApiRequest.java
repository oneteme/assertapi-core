package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;

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
public final class ApiRequest extends HttpRequest implements ComparableApi {

	private final Long id;
	private final String name;
	private final Integer version;
	private final String description; //case description
	private final ResponseComparisonConfig comparisonConfig; //nullable
	private final ExecutionConfig executionConfig;
	private final HttpRequest statbleApi;
	
	public ApiRequest(Long id, String name, Integer version, String description, 
			String uri, String method, Map<String, String> headers, int[] acceptableStatus, 
			ExecutionConfig executionConfig, ResponseComparisonConfig responseConfig, HttpRequest statbleApi) {
		super(uri, method, headers, acceptableStatus);
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
		this.comparisonConfig = responseConfig;
		this.statbleApi = statbleApi;
		this.executionConfig = ofNullable(executionConfig).orElseGet(ExecutionConfig::defaultConfig);
	}

	@Override
	public HttpRequest stableApi() {
		return ofNullable(statbleApi).orElse(this); 
	}

	@Override
	public HttpRequest latestApi() {
		return this;
	}
}
