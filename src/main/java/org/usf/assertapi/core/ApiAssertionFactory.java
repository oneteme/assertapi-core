package org.usf.assertapi.core;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNullElseGet;
import static org.usf.assertapi.core.RestTemplateBuilder.defaultAuthenticators;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import lombok.NonNull;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class ApiAssertionFactory {
	
	//init with default authenticators
	private final Map<String, Class<? extends ClientAuthenticator>> clientAuthenticators = new HashMap<>(defaultAuthenticators);
	private ResponseComparator comparator;
	private ServerConfig stableRelease;
	private ServerConfig latestRelease;
	private BiConsumer<ApiRequest, ComparisonResult> tracer;
	
	public ApiAssertionFactory register(@NonNull String name, @NonNull Class<? extends ClientAuthenticator> c) {
		clientAuthenticators.put(name, c);
		return this;
	}
	
	public ApiAssertionFactory comparingStatically(@NonNull ServerConfig latestRelease) {
		return comparing(null, latestRelease);
	}
	
	public ApiAssertionFactory comparing(ServerConfig stableRelease, @NonNull ServerConfig latestRelease) {
		this.stableRelease = stableRelease;
		this.latestRelease = latestRelease;
		return this;
	}
	
	public ApiAssertionFactory using(@NonNull ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionFactory trace(@NonNull BiConsumer<ApiRequest, ComparisonResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ResponseComparator build() {
		var map = unmodifiableMap(clientAuthenticators);
		var latestTemp = RestTemplateBuilder.build(latestRelease, map);
		var cmp = requireNonNullElseGet(comparator, ResponseComparator::new);
		cmp.setExecutor(stableRelease == null
				? new DisconnectedAssertionExecutor(latestTemp)
				: new ConnectedAssertionExecutor(RestTemplateBuilder.build(stableRelease, map), latestTemp));
		if(tracer != null) {
			cmp = new ResponseComparatorProxy(cmp, tracer);
		}
		return cmp;
	}
}
