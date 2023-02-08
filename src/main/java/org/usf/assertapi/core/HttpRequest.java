package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
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
		this.uri = ofNullable(uri).map(String::trim).orElse("");
		this.method = ofNullable(method).map(String::trim).map(String::toUpperCase).filter(not(String::isEmpty)).orElse(DEFAULT_METHOD);
		this.headers = headers;
		this.body = body;
		this.lazyBody = lazyBody;
	}
	
	/**@see HttpHeaders.getFirst(CONTENT_TYPE);*/
	public String getFirstHeader(String title) {
		if(!isEmpty(headers)) {
			var header = headers.get(title);
			if(!isEmpty(header)) {
				return header.get(0);  
			}
		}
		return null;
	}

	public String bodyAsString() {
		return ofNullable(body).map(String::new).orElse(null);
	}
	
	@Override
	public String toString() {
		return toRequestUri();
	}
	
	public String toRequestUri() {
		return new StringBuilder(50)
				.append("[").append(method).append("] ")
				.append(uri).toString();
	}
	
}