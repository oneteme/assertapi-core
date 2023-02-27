package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@JsonInclude(NON_NULL)
public class HttpRequest {
	
	static final String DEFAULT_URI = "";
	static final String DEFAULT_METHOD = "GET";
	static final int DEFAULT_STATUS = 200;

	private String uri = DEFAULT_URI;
	private String method = DEFAULT_METHOD;
	private Map<String, List<String>> headers;
	private byte[] body;
	private String lazyBody;
	//lazyBody => SELFT (null) | local(filename) | remote(UID) resource

	@Setter(AccessLevel.PACKAGE)
	private URI location; //must be injected after deserialization
	
	public HttpRequest setUri(String uri) {
		this.uri = ofNullable(uri).map(String::trim).orElse(DEFAULT_URI);
		return this;
	}
	
	public HttpRequest setMethod(String method) {
		this.method = ofNullable(method).map(String::trim).filter(not(String::isEmpty)).map(String::toUpperCase).orElse(DEFAULT_METHOD);
		return this;
	}
	
	public HttpRequest setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
		return this;
	}
	
	@JsonDeserialize(using = StringBytesDeserializer.class) 
	public HttpRequest setBody(byte[] body){
		this.body = body;
		return this;
	}

	@JsonSerialize(using = StringBytesSerializer.class )
	public byte[] getBody(){
		return body;
	}
	
	public HttpRequest setLazyBody(String lazyBody) {
		this.lazyBody = lazyBody;
		return this;
	}
	

	public String bodyAsString() {
		return ofNullable(body).map(String::new).orElse(null);
	}
	
	public String firstHeader(String title) {
		if(!isEmpty(headers)) {
			var header = headers.get(title);
			if(!isEmpty(header)) {
				return header.get(0);  
			}
		}
		return null;
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