package org.usf.assertapi.core;

import static java.lang.String.format;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@Setter
@NoArgsConstructor
public final class ServerConfig { //TODO make it immutable
	
	private String host;
	private int port;
	private ServerAuth auth;

	private ServerConfig(String host, int port, ServerAuth auth) {
		this.host = host;
		this.port = port;
		this.auth = auth;
	}

	public String buildRootUrl() {
    	return format("http%s://%s%s/", 
    			port == 443 ? "s" : "", host, 
    			port == 80 || port == 443 ? "" : ":" + port);
    }
	
	public static ServerConfig localServer(int port) {
		return localServer(port, null);
	}

	public static ServerConfig localServer(int port, ServerAuth auth) {
		return new ServerConfig("localhost", port, auth);
	}
	
	@Override
	public String toString() {
		var s = buildRootUrl();
		if(auth != null) {
			s+= " {" + auth + "}";
		}
		return s;
	}
}
