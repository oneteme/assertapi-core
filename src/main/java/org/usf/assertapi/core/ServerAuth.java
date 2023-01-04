package org.usf.assertapi.core;

import java.util.HashMap;

import lombok.NoArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@SuppressWarnings("serial")
@NoArgsConstructor
public final class ServerAuth extends HashMap<String, String> {

	/**
	 * 
	 * @see org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod
	 */
	public String getAuthMethod() {
		return get("type");
	}

	public String getToken() {
		return get("token");
	}

	public String getUsername() {
		return get("username");
	}

	public String getPassword() {
		return get("password");
	}

	public String getAccessTokenUrl() {
		return get("access-token-url");
	}
	
	@Override
	public String toString() {
		return "type=" + getAuthMethod();
	}
}
