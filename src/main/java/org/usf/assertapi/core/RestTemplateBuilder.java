package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.NO_AUTH;
import static org.usf.assertapi.core.Module.getClientAuthenticator;

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
	
	public static RestTemplate build(ServerConfig conf) {
		var method = ofNullable(conf.getAuth()).map(ServerAuth::getAuthMethod).orElse(null);
		ClientHttpRequestInitializer ri;
		if(method == null || NO_AUTH.name().equals(method)) {
			ri = chr-> {};
		}
		else {
			var auth = getClientAuthenticator(method);
			ri = chr-> auth.authorization(chr, conf);
		}
		return build(conf, ri);
	}
	
	public static RestTemplate build(ServerConfig conf, ClientHttpRequestInitializer initializer) {
		var rt = build(conf.buildRootUrl());
		rt.getClientHttpRequestInitializers().add(initializer);
		return rt;
	}

	public static RestTemplate build(String url) {
		var rt = new RestTemplate();
		RootUriTemplateHandler.addTo(rt, url);
		return rt;
	}

}