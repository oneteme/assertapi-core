package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Setter
@Getter
@JsonInclude(NON_NULL)
public class ApiRequest {

	private final Long id;
	private final String name;
	private final Integer version;
	private final String uri;
	private final String method;
	private final Map<String, String> headers;
	@JsonDeserialize(using = JsonStringDeserializer.class) 
	private String body; //not works in constructor 
	private final int[] acceptableStatus;
	private final ExecutionConfig execConfig;
	
	public ApiRequest(Long id, String name, Integer version, 
			String uri, String method, Map<String, String> headers,
			int[] referStatus, ExecutionConfig execConfig) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(String::trim).map(m-> m.trim().toUpperCase()).orElse("GET");
		this.headers = headers;
		this.acceptableStatus = ofNullable(referStatus).orElse(new int[] {200}); //OK or may be NotFound ?
		this.execConfig = ofNullable(execConfig).orElseGet(ExecutionConfig::defaultConfig);
	}
	
	public boolean hasHeaders() {
		return headers != null && !headers.isEmpty();
	}
	
	public ExecutionConfig executionConfig() {
		return execConfig;
	}

	public boolean acceptStatus(int status) {
		return IntStream.of(acceptableStatus).anyMatch(v-> v == status);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(100);
		if(id != null) {
			sb.append(id).append(" - ");
		}
		if(name != null) {
			sb.append(name).append(" : ");
		}
		return sb.append("[").append(method).append("] ").append(uri).toString();
	}
	
}