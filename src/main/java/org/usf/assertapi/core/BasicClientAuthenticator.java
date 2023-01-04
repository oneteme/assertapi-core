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
				requireNonEmpty(auth.getUsername(), ()-> BASIC + " require username"), 
				requireNonEmpty(auth.getPassword(), ()-> BASIC + " require password"));
	}
}
