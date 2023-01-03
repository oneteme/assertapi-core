package org.usf.assertapi.core;

import static java.lang.System.setProperty;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RuntimeEnvironementTest {
	
	private static final Map<String, String> variables = Map.of(
			"$env-user", "usf",
			"$env-os", "JARVIS",
			"$env-jre", "1.8",
			"$env-address", "0.0.0.0",
			"$env-branch", "release");
	
	@BeforeAll
	static void init() {
		setProperty("user.name", variables.get("$env-user"));
		setProperty("os.name", variables.get("$env-os"));
		setProperty("java.version", variables.get("$env-jre"));
		//TODO mock try Runtime + InetAddress
	}

	@Test
	void testBuild() {
		var re = RuntimeEnvironement.build();
		assertEquals(variables.get("$env-user"), re.getUser());
		assertEquals(variables.get("$env-os"), re.getOs());
		assertEquals(variables.get("$env-jre"), re.getJre());
	}

	@Test
	void testFrom() {
		RuntimeEnvironement.from(variables::get).push((k, v)->{
			assertEquals(variables.get(k), v);
		});
	}

	@Test
	void testWithUser() {
		assertEquals("user", RuntimeEnvironement.build().withUser("user").getUser());
	}
	
	@Test
	void testGetHostAddress() {
		assertDoesNotThrow(RuntimeEnvironement::getHostAddress);
	}

	
	@Test
	void testGetLocalBranch() {
		assertDoesNotThrow(RuntimeEnvironement::getLocalBranch);
	}

}
