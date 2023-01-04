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
	private BiConsumer<ComparableApi, ComparisonResult> tracer;
	
	public void register(@NonNull String name, @NonNull Class<? extends ClientAuthenticator> c) {
		clientAuthenticators.put(name, c);
	}
	
	public ApiAssertionFactory comparing(@NonNull ServerConfig stableRelease, @NonNull ServerConfig latestRelease) {
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
	
	public ApiAssertion build() {
		var cmp = requireNonNullElseGet(comparator, ResponseComparator::new);
		if(tracer != null) {
			cmp = new ResponseComparatorProxy(cmp, tracer);
		}
		return new ApiDefaultAssertion(cmp,
				RestTemplateBuilder.build(stableRelease, unmodifiableMap(clientAuthenticators)),
				RestTemplateBuilder.build(latestRelease, unmodifiableMap(clientAuthenticators)));
	}
}
