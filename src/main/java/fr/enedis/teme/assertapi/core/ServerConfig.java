package fr.enedis.teme.assertapi.core;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.valueOf;

import java.util.HashMap;

import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
public final class ServerConfig extends HashMap<String, String> {

	private static final String PORT_KEY = "port";
	private static final String HOST_KEY = "host";
	
	private ServerConfig(String host, int port) {
		put(HOST_KEY, host);
		put(PORT_KEY, valueOf(port));
	}

	public String buildRootUrl() {
    	
		int port = getPort();
    	return format("http%s://%s", 
    			port == 443 ? "s" : "", getHost()) + 
    			(port == 80 || port == 443 ? "" : ":" + port) + "/";
    }
	
	public static ServerConfig localServer(int port) {
		
		return new ServerConfig("localhost", port);
	}
	
	public String getHost() {
		return get(HOST_KEY);
	}
	
	public int getPort(){
		return parseInt(get(PORT_KEY));
	}

	public String getAuthMethod() {
		return get("auth-method");
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
	
}
