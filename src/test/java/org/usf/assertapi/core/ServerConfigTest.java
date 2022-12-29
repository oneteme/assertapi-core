package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.assertapi.core.ServerConfig.localServer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServerConfigTest {
	
	@ParameterizedTest
	@CsvSource({
	    "localhost,   80, http://localhost/",
	    "localhost,  443, https://localhost/",
	    "localhost, 9001, http://localhost:9001/",
	    "99.83.254.144,   80, http://99.83.254.144/",
	    "99.83.254.144,  443, https://99.83.254.144/",
	    "99.83.254.144, 9001, http://99.83.254.144:9001/"
	})
	void testBuildRootUrl(String host, int port, String expected) {
		var s = new ServerConfig();
		s.setHost(host);
		s.setPort(port);
		assertEquals(s.buildRootUrl(), expected);
	}

	@ParameterizedTest
	@CsvSource({
	    "80, http://localhost/",
	    "443, https://localhost/",
	    "9001, http://localhost:9001/",
	})
	void testLocalServer(int port, String expected) {
		assertEquals(localServer(port).buildRootUrl(), expected);
	}
}
