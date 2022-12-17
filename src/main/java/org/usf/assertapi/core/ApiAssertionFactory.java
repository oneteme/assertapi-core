package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

public final class ApiAssertionFactory {

	private ResponseComparator comparator;
	private ServerConfig stableRelease;
	private ServerConfig latestRelease;
	private Consumer<AssertionResult> tracer;
	private boolean loggable = true;
	
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
	
	public ApiAssertionFactory disableLog() {
		loggable = false;
		return this;
	}
	
	public ApiAssertion build() {
		requireNonNull(comparator);
		requireNonNull(stableRelease);
		requireNonNull(latestRelease);
		var cmp = tracer == null ? comparator 
				: new ResponseProxyComparator(comparator, tracer, 
						new RequestExecution(stableRelease.buildRootUrl()), 
						new RequestExecution(latestRelease.buildRootUrl()));
		if(loggable) {
			cmp = new LoggableResponseComparator(cmp);
		}
		return new DefaultApiAssertion(cmp,
				RestTemplateBuilder.build(stableRelease),
				RestTemplateBuilder.build(latestRelease));
	}
}
