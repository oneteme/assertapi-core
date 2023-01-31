package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonIgnoreProperties(value = "location")
public final class ApiRequest extends HttpRequest {

	private final Long id;
	private final String name;
	private final Integer version;
	private final String description; //case description
	private final int[] acceptableStatus;
	private final DataComparator<?> contentComparator; //nullable
	private final ExecutionConfig executionConfig;
	private final HttpRequest remoteApi;
	private final StaticResponse staticResponse;
	
	private URI location; //must be injected after deserialization
	
	public ApiRequest(Long id, String name, Integer version, String description, 
			String uri, String method, Map<String, List<String>> headers, @JsonDeserialize(using = StringBytesDeserializer.class) byte[] body, String lazyBody, 
			int[] acceptableStatus, ExecutionConfig executionConfig, DataComparator<?> contentComparator, HttpRequest remoteApi, StaticResponse staticResponse) {
		
		super(uri, method, headers, body, lazyBody);
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
		this.acceptableStatus = acceptableStatus == null || acceptableStatus.length == 0 ? new int[] {DEFAULT_STATUS} : acceptableStatus; //OK or may be NotFound ?
		this.contentComparator = contentComparator;
		this.executionConfig = requireNonNullElseGet(executionConfig, ExecutionConfig::new);
		this.remoteApi = remoteApi;
		this.staticResponse = staticResponse;
	}
	
	public HttpRequest latestApi() {
		return this;
	}
	
	public HttpRequest stableApi() {
		return requireNonNullElse(remoteApi, this); // RUN : TNR == MIGRATION
	}
	
	public StaticResponse staticResponse() {
		return staticResponse;
	}

	public boolean acceptStatus(int status) {
		return IntStream.of(acceptableStatus).anyMatch(v-> v == status);
	}
	
	public ApiRequest withLocation(URI location) {
		this.location = location;
		return this;
	}
	
	@Override
	public String toString() {
		var sb = new StringBuilder();
		if(name != null) {
			sb.append("[").append(name).append("] ");
		}
		if(description != null) {
			sb.append(description);
		}
		return sb.length() == 0 ? super.toString() : sb.toString(); //isEmpty java15
	}
}
