package org.usf.assertapi.core;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpHeaders;

class BearerClientAuthenticatorTest {
	
	private static final String dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
			+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
			+ ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
	
	private static final String anothToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
			+ ".eyJtYWlsIjoidXNmLmFsYW1pQGdtYWlsLmNvbSIsIm5hbWUiOiJ1JGYiLCJpYXQiOjE1MTYyMzkwMjJ9"
			+ ".qUKtNcdBobZuOZzjfvf0XgfzkqcQ8s7iRPzaLOvUN8w";

	@ParameterizedTest
	@CsvSource({
		dummyToken+",Bearer " +dummyToken,
		anothToken+",Bearer " +anothToken
	})
	void testAuthorization(String token, String expected) {
		var headers = new HttpHeaders();
		var auth = new ServerAuth();
		auth.put("token", token);
		new BearerClientAuthenticator().authorization(headers, auth);
		assertEquals(1, headers.size()); //only one header
		assertEquals(asList(expected), headers.get("Authorization"));
	}

	@Test
	void testGetType() {
		assertEquals("BEARER", new BearerClientAuthenticator().getType());
	}
}
