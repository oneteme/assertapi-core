package org.usf.assertapi.core;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpHeaders;
import org.usf.assertapi.core.Utils.EmptyValueException;

class BasicClientAuthenticatorTest {
	
	@Test
	void testAuthorization_empty() {
		var headers = new HttpHeaders();
		var auth = new ServerAuth();
		var authenticator = new BasicClientAuthenticator();
		assertThrows(EmptyValueException.class, ()-> authenticator.authorization(headers, auth)); //username null
		auth.put("username", "");
		assertThrows(EmptyValueException.class, ()-> authenticator.authorization(headers, auth)); //username empty
		auth.put("username", "dummyUser");
		assertThrows(EmptyValueException.class, ()-> authenticator.authorization(headers, auth)); //password null
		auth.put("password", "");
		assertThrows(EmptyValueException.class, ()-> authenticator.authorization(headers, auth)); //password empty
		auth.put("password", "dummyPass");
		assertDoesNotThrow(()-> new BasicClientAuthenticator().authorization(headers, auth));
	}

	@ParameterizedTest
	@CsvSource({
	    "dummyUser,dummyPass,Basic ZHVtbXlVc2VyOmR1bW15UGFzcw==",
	    "username1,password0,Basic dXNlcm5hbWUxOnBhc3N3b3JkMA==",
	    "admin,12345,Basic YWRtaW46MTIzNDU="
	})
	void testAuthorization(String username, String password, String expected) {
		var headers = new HttpHeaders();
		var auth = new ServerAuth();
		auth.put("username", username);
		auth.put("password", password);
		new BasicClientAuthenticator().authorization(headers, auth);
		assertEquals(1, headers.size()); //only one header
		assertEquals(asList(expected), headers.get("Authorization"));
	}

}
