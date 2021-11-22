package fr.enedis.teme.assertapi.core;

import static java.util.Optional.ofNullable;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class HttpRequest {
	
	private String uri;
	private String method;
	private Map<String, String> headers;
	@JsonDeserialize(using = JsonStringDeserializer.class)
	private String body;
	private RequestOutput output;
	
	public void build() {
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(m-> m.trim().toUpperCase()).orElse("GET");
		this.output = ofNullable(output).orElseGet(RequestOutput::new).build();
	}

	public HttpRequest copy() {
		var hr = new HttpRequest();
		hr.setUri(uri);
		hr.setMethod(method);
		hr.setBody(body);
		hr.setHeaders(headers);
		hr.setOutput(output.copy());
		return hr;
	}

	@Override
	public String toString() {
		return "["+method+"]" + " " + uri;
	}
	
}