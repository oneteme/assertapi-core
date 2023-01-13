package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
	
	static final int DEFAULT_STATUS = 200;
	static final String DEFAULT_METHOD = "GET";

	private final String uri;
	private final String method;
	private final Map<String, List<String>> headers;
	@JsonSerialize(using = StringBytesSerializer.class )
	private final byte[] body;
	private final String lazyBody;
	
	public HttpRequest(String uri, String method, Map<String, List<String>> headers, 
			@JsonDeserialize(using = StringBytesDeserializer.class) byte[] body, String lazyBody) {
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(String::trim).map(m-> m.trim().toUpperCase()).orElse(DEFAULT_METHOD);
		this.headers = headers;
		this.body = body;
		this.lazyBody = lazyBody;
	}
	
	public boolean hasHeaders() {
		return headers != null && !headers.isEmpty();
	}
	
	public String getHeader(String title) {
		if(headers != null) {
			var header = headers.get(title);
			if(!isEmpty(header)) {
				return header.get(0);  /**@see HttpHeaders.getFirst(CONTENT_TYPE);*/
			}
		}
		return null;
	}

	public String bodyAsString() {
		return body == null ? null : new String(body);
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