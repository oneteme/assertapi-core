package fr.enedis.teme.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(NON_NULL)
public final class ApiRequest {
	
	private final Long id;
	private final String uri;
	private final String method;
	private final Map<String, String> headers;
	@JsonDeserialize(using = JsonStringDeserializer.class) 
	private String body; //not works in constructor 
	private final String charset; //UTF-8|ISO-8859-1|UTF-16
	private final String name;
	private final String description;
	private final AssertionConfig configuration;
	
	public ApiRequest(Long id, String uri, String method, Map<String, String> headers, String charset,
			String name, String description, AssertionConfig configuration) {
		this.id = id;
		this.name = name;
		this.uri = ofNullable(uri).map(String::trim).map(u-> u.startsWith("/") ? u : "/" + u)
				.orElseThrow(()-> new IllegalArgumentException("URI connot be null"));
		this.method = ofNullable(method).map(m-> m.trim().toUpperCase()).orElse("GET");
		this.headers = headers;
		this.charset = ofNullable(charset).map(c-> c.trim().toUpperCase().replace('_', '-')).orElse(UTF_8.name());
		this.description = description;
		this.configuration = ofNullable(configuration).orElseGet(AssertionConfig::defaultConfig);
	}
	
	public boolean hasHeaders() {
		return headers != null && !headers.isEmpty();
	}

	public boolean hasBody() {
		return body != null;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder(100);
		if(id != null) {
			sb.append(id).append(" - ");
		}
		if(description != null) {
			sb.append(description).append(" : ");
		}
		return sb.append("[").append(method).append("] ").append(uri).toString();
	}
}