package fr.enedis.teme.assertapi.core;

import java.util.HashMap;

import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
public class ServerAuth extends HashMap<String, String> {

	public String getAuthMethod() {
		return get("type"); //basic|token|novaBasic|novaToken
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
