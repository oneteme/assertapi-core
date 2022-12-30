package org.usf.assertapi.core;

import org.springframework.http.client.ClientHttpRequest;

import lombok.Getter;

public interface ClientAuthenticator {
	
	String getType();
	
	void authorization(ClientHttpRequest request, ServerConfig conf);
	
	@Getter
	enum ServerAuthMethod {
	    NO_AUTH,
	    BASIC,
	    BEARER
	}
}
