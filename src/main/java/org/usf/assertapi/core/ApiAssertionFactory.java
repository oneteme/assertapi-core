package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import java.util.function.BiConsumer;

/**
 * 
 * @author u$f
 *
 */
public final class ApiAssertionFactory {

	private ResponseComparator comparator;
	private ServerConfig stableRelease;
	private ServerConfig latestRelease;
	private BiConsumer<ApiRequest, ApiCompareResult> tracer;
	
	public ApiAssertionFactory comparing(ServerConfig stableRelease, ServerConfig latestRelease) {
		this.stableRelease = stableRelease;
		this.latestRelease = latestRelease;
		return this;
	}
	
	public ApiAssertionFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionFactory trace(BiConsumer<ApiRequest, ApiCompareResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ApiAssertion build() {
		var cmp = ofNullable(comparator).orElseGet(ResponseComparator::new);
		if(tracer != null) {
			cmp = new ResponseComparatorProxy(cmp, tracer);
		}
		return new ApiDefaultAssertion(cmp,
				RestTemplateBuilder.build(requireNonNull(stableRelease)),
				RestTemplateBuilder.build(requireNonNull(latestRelease)));
	}
}
