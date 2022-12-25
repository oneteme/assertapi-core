package org.usf.assertapi.core;

import static org.usf.assertapi.core.RestTemplateClientHttpRequestInitializer.init;

import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.web.client.RestTemplate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestTemplateBuilder {
	
	public static RestTemplate build(String url) {
		var rt = new RestTemplate();
		RootUriTemplateHandler.addTo(rt, url);
		return rt;
	}
	
	public static RestTemplate build(ServerConfig conf) {
		return build(conf, init(conf));
	}
	
	public static RestTemplate build(ServerConfig conf, ClientHttpRequestInitializer initializer) {
		var rt = new RestTemplate();
		RootUriTemplateHandler.addTo(rt, conf.buildRootUrl());
		rt.getClientHttpRequestInitializers().add(initializer);
		return rt;
	}

}