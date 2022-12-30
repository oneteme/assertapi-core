package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
	private final TypeComparatorConfig<?> typeComparatorConfig; //nullable
	private final ExecutionConfig executionConfig;
	private final HttpRequest stableApi;
	
	public ApiRequest(Long id, String name, Integer version, String description, 
			String uri, String method, Map<String, String> headers, @JsonDeserialize(using = JsonStringDeserializer.class) String body, 
			int[] acceptableStatus, ExecutionConfig executionConfig, TypeComparatorConfig<?> typeComparatorConfig, HttpRequest statbleApi) {
		super(uri, method, headers, body, acceptableStatus);
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
		this.typeComparatorConfig = typeComparatorConfig;
		this.stableApi = statbleApi;
		this.executionConfig = requireNonNullElseGet(executionConfig, ExecutionConfig::new);
	}

	@Override
	public HttpRequest stableApi() {
		return ofNullable(stableApi).orElse(this); 
	}

	@Override
	public HttpRequest latestApi() {
		return this;
	}
	
	@Override
	public String toString() {
		var sb = new StringBuilder();
		if(name != null) {
			sb.append("[").append(name).append("] ");
		}
		if(description != null) {
			sb.append(description);
		}
		var s = sb.toString();
		return s.isEmpty() ? super.toString() : s;
	}
}
