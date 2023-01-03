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
public interface ClientAuthenticator extends PolymorphicType {
	
	String getType();
	
	default void authorization(ClientHttpRequest req, ServerConfig conf) {
		authorization(req.getHeaders(), conf.getAuth());
	}

	void authorization(HttpHeaders headers, ServerAuth auth);
	
	@Getter
	enum ServerAuthMethod {
	    NO_AUTH,
	    BASIC,
	    BEARER
	}
}
