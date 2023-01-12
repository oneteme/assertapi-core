package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;
import static org.usf.assertapi.core.Utils.isEmpty;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.usf.assertapi.core.Utils.TooManyValueException;

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

	private final String uri;
	private final String method;
	private final Map<String, List<String>> headers;
	@JsonSerialize(using = StringBytesSerializer.class )
	private final byte[] body; //TD change type => Object | byte[]
	private final int[] acceptableStatus;
	
	public HttpRequest(String uri, String method, Map<String, List<String>> headers, 
			@JsonDeserialize(using = StringBytesDeserializer.class) byte[] body, int... acceptableStatus) {
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(String::trim).map(m-> m.trim().toUpperCase()).orElse("GET");
		this.headers = headers;
		this.body = body;
		this.acceptableStatus = acceptableStatus == null || acceptableStatus.length == 0 ? new int[] {200} : acceptableStatus; //OK or may be NotFound ?
	}

	public boolean acceptStatus(int status) {
		return IntStream.of(acceptableStatus).anyMatch(v-> v == status);
	}
	
	public int requireUniqueStatus() {
		var status = requireNonEmpty(acceptableStatus, "HttpRequest", "acceptableStatus");
		if(status.length == 1) {
			return status[0];
		}
		throw new TooManyValueException("HttpRequest", "acceptableStatus");
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