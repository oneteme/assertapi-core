package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

class StaticResponseTest {

	private final StaticResponse response = new StaticResponse();
	
	@Test
	void testSetStatus() {
		assertEquals(200, response.getStatus());
		assertEquals(404, response.setStatus(404).getStatus());
		assertEquals(200, response.setStatus(null).getStatus());
	}
		
	@Test
	void testGetUri() {
		assertThrowsExactly(UnsupportedOperationException.class, ()-> response.getUri());
	}

	@Test
	void testSetUri() {
		assertThrowsExactly(UnsupportedOperationException.class, ()-> response.setUri(""));
	}
	
	@Test
	void testGetMethod() {
		assertThrowsExactly(UnsupportedOperationException.class, ()-> response.getMethod());
	}
	
	@Test
	void testSetMethod() {
		assertThrowsExactly(UnsupportedOperationException.class, ()-> response.setMethod(""));
	}
}
