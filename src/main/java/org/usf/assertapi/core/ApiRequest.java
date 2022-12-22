package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author u$f
 *
 */
@Setter
@Getter
@JsonInclude(NON_NULL)
public final class ApiRequest {

	private final Long id;
	private final String uri;
	private final String method;
	private final Map<String, String> headers;
	@JsonDeserialize(using = JsonStringDeserializer.class) 
	private String body; //not works in constructor 
	private final String name;
	private final String description;
	private final short referStatus;
	private final ResponseCompareConfig respConfig;
	private final ExecutionConfig execConfig;
	
	public ApiRequest(Long id, String uri, String method, Map<String, String> headers,
			String name, String description, Short referStatus, ResponseCompareConfig respConfig, ExecutionConfig configuration) {
		this.id = id;
		this.name = name;
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(m-> m.trim().toUpperCase()).orElse("GET");
		this.headers = headers;
		this.description = description;
		this.referStatus = ofNullable(referStatus).orElse((short)200); //OK by default
		this.execConfig = ofNullable(configuration).orElseGet(ExecutionConfig::defaultConfig);
		this.respConfig = respConfig; //TODO nullable ?
	}
	
	public boolean hasHeaders() {
		return headers != null && !headers.isEmpty();
	}
	
	public ExecutionConfig executionConfig() {
		return execConfig;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(100);
		if(id != null) {
			sb.append(id).append(" - ");
		}
		if(description != null) {
			sb.append(description).append(" : ");
		}
		return sb.append("[").append(method).append("] ").append(uri).toString();
	}
	
}