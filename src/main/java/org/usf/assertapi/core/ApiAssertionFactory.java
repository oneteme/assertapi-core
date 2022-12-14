package org.usf.assertapi.core;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.ofNullable;
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
	private BiConsumer<ComparableApi, ComparisonResult> tracer;
	
	public void register(@NonNull String name, @NonNull Class<? extends ClientAuthenticator> c) {
		clientAuthenticators.put(name, c);
	}
	
	public ApiAssertionFactory comparingWithStaticResponse(@NonNull ServerConfig latestRelease) {
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
	
	public ApiAssertionFactory trace(@NonNull BiConsumer<ComparableApi, ComparisonResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ApiAssertionExecutor build() {
		var cmp = requireNonNullElseGet(comparator, ResponseComparator::new);
		if(tracer != null) {
			cmp = new ResponseComparatorProxy(cmp, tracer);
		}
		var map = unmodifiableMap(clientAuthenticators);
		var stableTemp = ofNullable(stableRelease).map(c-> RestTemplateBuilder.build(c, map)).orElse(null);
		var latestTemp = RestTemplateBuilder.build(latestRelease, map);
		return new ApiAssertionExecutor(cmp, stableTemp, latestTemp);
	}
}
