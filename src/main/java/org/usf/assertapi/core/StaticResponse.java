package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@JsonIgnoreProperties({"uri", "method"}) //unused fields check that
public final class StaticResponse extends HttpRequest {

	private final int status;
	
	public StaticResponse(Integer status, Map<String, List<String>> headers, 
			@JsonDeserialize(using = StringBytesDeserializer.class) byte[] body, String lazyBody) { 
		super(null, null, headers, body, lazyBody);
		this.status = requireNonNullElse(status, DEFAULT_STATUS);
	}
	
	public StaticResponse withBody(byte[] body) {
		return new StaticResponse(status, getHeaders(), body, getLazyBody());
	}
	
	@Override
	public String getUri() {
		throw new UnsupportedOperationException("unsupported uri field");
	}
	
	@Override
	public String getMethod() {
		throw new UnsupportedOperationException("unsupported method field");
	}
}
