package org.usf.assertapi.core;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@FunctionalInterface
public interface ClientAuthenticator {

	void authorization(HttpHeaders headers, ServerAuth auth);
	
	default void authorization(ClientHttpRequest req, ServerConfig conf) {
		authorization(req.getHeaders(), conf.getAuth());
	}
	
	@Getter
	enum ServerAuthMethod {
	    BASIC,
	    BEARER
	}
}
