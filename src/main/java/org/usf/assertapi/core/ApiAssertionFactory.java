package org.usf.assertapi.core;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;
import static org.usf.assertapi.core.AssertionContext.CTX;
import static org.usf.assertapi.core.AssertionContext.CTX_ID;
import static org.usf.assertapi.core.AssertionContext.buildContext;

import java.util.function.Consumer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public final class ApiAssertionFactory {

	private ResponseComparator comparator;
	private ServerConfig stableRelease;
	private ServerConfig latestRelease;
	private Consumer<AssertionResult> tracer;
	
	public ApiAssertionFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionFactory comparing(ServerConfig stableRelease, ServerConfig latestRelease) {
		this.stableRelease = stableRelease;
		this.latestRelease = latestRelease;
		return this;
	}
	
	public ApiAssertionFactory trace(Consumer<AssertionResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ApiAssertionFactory traceOn(String url) {
		try {
			var template = new RestTemplate(); //put only
			var hds = new HttpHeaders();
			hds.add(CTX, buildContext().toHeader());
			var ctx = template.exchange(url, GET, new HttpEntity<>(hds), String.class).getBody();
			template.setClientHttpRequestInitializers(singletonList(req-> req.getHeaders().set(CTX_ID, ctx)));
			this.tracer = tr-> template.put(url, tr);
		} catch(Exception e) {
			log.warn("error while connecting to " + url, e);
			this.tracer = tr-> log.warn("cannot trace {} on {}", tr, url);
		}
		return this;
	}
	
	public ApiAssertion build() {
		requireNonNull(comparator);
		var cmp = tracer == null ? comparator 
				: new ResponseProxyComparator(comparator, tracer, stableRelease, latestRelease);
		return new DefaultApiAssertion(
				RestTemplateBuilder.build(requireNonNull(stableRelease)),
				RestTemplateBuilder.build(requireNonNull(latestRelease)),
				new LoggableResponseComparator(cmp)); 
	}
	
}
