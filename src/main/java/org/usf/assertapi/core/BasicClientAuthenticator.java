package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BASIC;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import org.springframework.http.client.ClientHttpRequest;

public class BasicClientAuthenticator implements ClientAuthenticator {

	@Override
	public void authorization(ClientHttpRequest request, ServerConfig conf) {
		var auth = requireNonNull(conf.getAuth());
		request.getHeaders().setBasicAuth(
				requireNonEmpty(auth.getUsername(), ()-> getType() + " require username"), 
				requireNonEmpty(auth.getPassword(), ()-> getType() + " require password"));
	}
	
	@Override
	public String getType() {
		return BASIC.name();
	}
}
