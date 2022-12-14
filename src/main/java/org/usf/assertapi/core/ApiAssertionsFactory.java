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

@NoArgsConstructor
public final class ApiAssertionsFactory {

	private ResponseComparator comparator;
	private ServerConfig exServer;
	private ServerConfig acServer;
	private Consumer<ApiAssertionsResult> tracer;
	
	public ApiAssertionsFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionsFactory comparing(ServerConfig expectedServerConf, ServerConfig actualServerConf) {
		this.exServer = expectedServerConf;
		this.acServer = actualServerConf;
		return this;
	}
	
	public ApiAssertionsFactory trace(Consumer<ApiAssertionsResult> tracer) {
		this.tracer = tracer;
		return this;
	}
	
	public ApiAssertionsFactory traceOn(String url) {
		var template = new RestTemplate(); //put only
		var hds = new HttpHeaders();
		hds.add(CTX, buildContext().toHeader());
		var ctx = template.exchange(url + "/register", GET, new HttpEntity<>(hds), String.class).getBody();
		template.setClientHttpRequestInitializers(singletonList(req-> req.getHeaders().set(CTX_ID, ctx)));
		this.tracer = tr-> template.put(url, tr);
		return this;
	}
	
	public ApiAssertions build() {
		
		requireNonNull(comparator);
		return new DefaultApiAssertions(
				RestTemplateBuilder.build(requireNonNull(exServer)),
				RestTemplateBuilder.build(requireNonNull(acServer)),
				tracer == null 
					? r-> comparator 
					: r-> new ResponseProxyComparator(comparator, tracer, exServer, acServer, r)); 
	}
	
}
