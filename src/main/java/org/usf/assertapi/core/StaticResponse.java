package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;

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
		throw unsupportedOperation("setUri");
	}
	
	@Override
	public String getUri() {
		throw unsupportedOperation("getUri");
	}
	
	@Override
	public StaticResponse setMethod(String method) {
		throw unsupportedOperation("setMethod");
	}
	
	@Override
	public String getMethod() {
		throw unsupportedOperation("getMethod");
	}
	
	private UnsupportedOperationException unsupportedOperation(String method) {
		return new UnsupportedOperationException(getClass().getCanonicalName() + "::" + method);
	}
}
