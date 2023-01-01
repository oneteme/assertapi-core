package org.usf.assertapi.core;

import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BASIC;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import org.springframework.http.HttpHeaders;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class BasicClientAuthenticator implements ClientAuthenticator {

	@Override
	public void authorization(HttpHeaders headers, ServerAuth auth) {
		headers.setBasicAuth(
				requireNonEmpty(auth.getUsername(), ()-> getType() + " require username"), 
				requireNonEmpty(auth.getPassword(), ()-> getType() + " require password"));
	}
	
	@Override
	public String getType() {
		return BASIC.name();
	}
}
