package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
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
public class HttpRequest {

	private final String uri;
	private final String method;
	private final Map<String, String> headers;
	@JsonRawValue
	private final String body;
	private final int[] acceptableStatus;
	
	public HttpRequest(String uri, String method, Map<String, String> headers,
			@JsonDeserialize(using = JsonStringDeserializer.class) String body, int... acceptableStatus) {
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(String::trim).map(m-> m.trim().toUpperCase()).orElse("GET");
		this.headers = headers;
		this.body = body;
		this.acceptableStatus = acceptableStatus == null || acceptableStatus.length == 0 ? new int[] {200} : acceptableStatus; //OK or may be NotFound ?
	}
	
	public boolean hasHeaders() {
		return headers != null && !headers.isEmpty();
	}

	public boolean acceptStatus(int status) {
		return IntStream.of(acceptableStatus).anyMatch(v-> v == status);
	}

	@Override
	public String toString() {
		return toRequestUri();
	}
	
	public String toRequestUri() {
		return new StringBuilder(100)
				.append("[").append(method).append("] ")
				.append(uri).toString();
	}
	
}