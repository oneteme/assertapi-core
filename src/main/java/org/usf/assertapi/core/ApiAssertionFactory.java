package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

/**
 * 
 * @author u$f
 *
 */
public final class ApiAssertionFactory {

	private ResponseComparator comparator;
	private ServerConfig stableRelease;
	private ServerConfig latestRelease;
	private Consumer<AssertionResult> tracer;
	
	public ApiAssertionFactory comparing(ServerConfig stableRelease, ServerConfig latestRelease) {
		this.stableRelease = stableRelease;
		this.latestRelease = latestRelease;
		return this;
	}
	
	public ApiAssertionFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionFactory trace(Consumer<AssertionResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ApiAssertion build() {
		var cmp = tracer == null 
				? comparator 
				: new ResponseProxyComparator(requireNonNull(comparator), tracer);
		return new ApiDefaultAssertion(cmp,
				RestTemplateBuilder.build(requireNonNull(stableRelease)),
				RestTemplateBuilder.build(requireNonNull(latestRelease)));
	}
}
