package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.usf.assertapi.core.ExecutionConfig.DEFAULT_CONFIG;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@Setter
@JsonInclude(NON_NULL)
@JsonIgnoreProperties("location")
public final class ApiRequest extends HttpRequest {
	
	private static final String DEFAULT_COMPARTOR_KEY = "*";

	private Long id;
	private String name;
	private Integer version;
	private String description; //case description
	private int[] accept = {DEFAULT_STATUS};
	private Map<String, ModelComparator<?>> comparators = emptyMap(); //nullable
	//TD private Map<String, ModelComparator<?>> headersCompartors;
	private ExecutionConfig execution = DEFAULT_CONFIG;
	private HttpRequest stable;
	private StaticResponse response;
	
	private String lazyComparators;

	public void setExecution(ExecutionConfig config) {
		this.execution = requireNonNullElse(config, DEFAULT_CONFIG);
	}
	
	public void setAccept(int[] accept) {
		this.accept = isEmpty(accept) ? new int[] {DEFAULT_STATUS} : accept; //OK or may be NotFound ?
	}
	
	public void setComparator(ModelComparator<?> comparator) {
		this.comparators = Map.of(DEFAULT_COMPARTOR_KEY, comparator);
	}

	public void setComparators(Map<String, ModelComparator<?>> comparators) {
		this.comparators = requireNonNullElseGet(comparators, Collections::emptyMap);
	}
	
	public ModelComparator<?> comparator(int status) {
		var key = ""+status;
		return comparators.containsKey(key) 
				? comparators.get(key)
				: comparators.get(DEFAULT_COMPARTOR_KEY); // optional comparator ==> nullable
	}
	
	public HttpRequest latest() {
		return this;
	}

	public HttpRequest stable() {
		return requireNonNullElse(stable, this); // RUN : TNR == MIGRATION
	}
	
	public StaticResponse response() {
		return response;
	}

	public boolean accept(int status) {
		return IntStream.of(accept).anyMatch(v-> v == status);
	}
	
	public ApiRequest withLocation(URI location) {
		if(response != null) {
			response.setLocation(location);
		}
		if(stable != null) {
			stable.setLocation(location);
		}
		setLocation(location);
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
