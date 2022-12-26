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
	 * Get the auth method type (<b>basic</b> | <b>token</b> | <b>nova_basic</b> | <b>nova_token</b>)
	 * @return auth method type
	 */
	public String getAuthMethod() {
		return get("type"); //basic|token|novabasic|nova_token
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
