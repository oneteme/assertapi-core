package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BASIC;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BEARER;

import java.util.Map;
import java.util.NoSuchElementException;

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
	
	static final Map<String, Class<? extends ClientAuthenticator>> defaultAuthenticators = Map.of(//unmodifiable
			BASIC.name(), BasicClientAuthenticator.class,
			BEARER.name(), BearerClientAuthenticator.class);
	
	public static RestTemplate build(ServerConfig conf) {
		return build(conf, defaultAuthenticators);
	}
	
	public static RestTemplate build(ServerConfig conf, Map<String, Class<? extends ClientAuthenticator>> clientAuthenticatorMap) {
		var rt = build(conf.buildRootUrl());
		var method = ofNullable(conf.getAuth()).map(ServerAuth::getAuthMethod).orElse(null);
		if(method != null) {
			var clazz = clientAuthenticatorMap.get(method);
			if(clazz == null) {
				throw new NoSuchElementException("no such class for " + method);
			}
			var auth = newInstance(clazz);
			rt.getClientHttpRequestInitializers().add(req-> auth.authorization(req, conf));
		}
		return rt;
	}
	
	public static RestTemplate build(String url) {
		var rt = new RestTemplate();
		RootUriTemplateHandler.addTo(rt, url);
		return rt;
	}
	
	private static ClientAuthenticator newInstance(Class<? extends ClientAuthenticator> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("error while instantiating " + clazz.getName(), e);
		}
	}

}