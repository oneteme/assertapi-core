package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;
import static org.usf.assertapi.core.Utils.unsupportedMethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

	private int status = DEFAULT_STATUS;
	
	public StaticResponse setStatus(Integer status) { 
		this.status = requireNonNullElse(status, DEFAULT_STATUS);
		return this;
	}
	
	@Override
	public StaticResponse setUri(String uri) {
		throw unsupportedMethod(getClass(), "setUri");
	}
	
	@Override
	public String getUri() {
		throw unsupportedMethod(getClass(), "getUri");
	}
	
	@Override
	public StaticResponse setMethod(String method) {
		throw unsupportedMethod(getClass(), "setMethod");
	}
	
	@Override
	public String getMethod() {
		throw unsupportedMethod(getClass(), "getMethod");
	}
}
