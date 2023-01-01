package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.NO_AUTH;
import static org.usf.assertapi.core.Module.getClientAuthenticator;

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
		var rt = build(conf.buildRootUrl());
		var method = ofNullable(conf.getAuth()).map(ServerAuth::getAuthMethod).orElse(null);
		if(method != null && !NO_AUTH.name().equals(method)) {
			var auth = getClientAuthenticator(method);
			rt.getClientHttpRequestInitializers().add(req-> auth.authorization(req, conf));
		}
		return rt;
	}
	
	public static RestTemplate build(String url) {
		var rt = new RestTemplate();
		RootUriTemplateHandler.addTo(rt, url);
		return rt;
	}

}