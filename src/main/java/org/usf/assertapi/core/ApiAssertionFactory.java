package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;

import java.util.function.BiConsumer;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class ApiAssertionFactory {

	private ResponseComparator comparator;
	private ServerConfig stableRelease;
	private ServerConfig latestRelease;
	private BiConsumer<ComparableApi, ComparisonResult> tracer;
	
	public ApiAssertionFactory comparing(ServerConfig stableRelease, ServerConfig latestRelease) {
		this.stableRelease = stableRelease;
		this.latestRelease = latestRelease;
		return this;
	}
	
	public ApiAssertionFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionFactory trace(BiConsumer<ComparableApi, ComparisonResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ApiAssertion build() {
		var cmp = requireNonNullElseGet(comparator, ResponseComparator::new);
		if(tracer != null) {
			cmp = new ResponseComparatorProxy(cmp, tracer);
		}
		return new ApiDefaultAssertion(cmp,
				RestTemplateBuilder.build(requireNonNull(stableRelease)),
				RestTemplateBuilder.build(requireNonNull(latestRelease)));
	}
}
