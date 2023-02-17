package org.usf.assertapi.core;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.HttpHeaders;
import org.usf.assertapi.core.Utils.EmptyValueException;

class BasicClientAuthenticatorTest {
	
	@NullSource
	@EmptySource
	@ParameterizedTest
	void testAuthorization_bad_username(String username) {
		String msg = "BASIC : require [username] field";
		var headers = new HttpHeaders();
		var auth = new ServerAuth();
		var authenticator = new BasicClientAuthenticator();
		auth.put("username", username);
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> authenticator.authorization(headers, auth));
	}
	
	@NullSource
	@EmptySource
	@ParameterizedTest
	void testAuthorization_bad_password(String password) {
		String msg = "BASIC : require [password] field";
		var headers = new HttpHeaders();
		var auth = new ServerAuth();
		var authenticator = new BasicClientAuthenticator();
		auth.put("username", "dummy");
		auth.put("password", password);
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> authenticator.authorization(headers, auth));
	}
	
	@ParameterizedTest
	@CsvSource({
	    "dummy,dummy,Basic ZHVtbXk6ZHVtbXk=",
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
