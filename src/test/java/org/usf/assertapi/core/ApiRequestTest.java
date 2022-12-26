package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ApiRequestTest {
	
	@Test
	void testContructor() {

		assertThrows(IllegalArgumentException.class, ()-> new HttpRequest(null, null, null, null));
		
		var api = new HttpRequest("", null, null, null);
		assertNull(api.getHeaders());
		assertNull(api.getBody());
		assertEquals("/", api.getUri());
		assertEquals("GET", api.getMethod());
		assertArrayEquals(new int[] {200}, api.getAcceptableStatus());

		api = new HttpRequest("v1/api", "PUT", Map.of("hdr1", "value1"), new int[] {500});
		api.setBody("[]");
		assertEquals(Map.of("hdr1", "value1"), api.getHeaders());
		assertEquals("[]", api.getBody());
		assertEquals("/v1/api", api.getUri());
		assertEquals("PUT", api.getMethod());
		assertArrayEquals( new int[] {500}, api.getAcceptableStatus());
	}

	@Test
	void testHasHeaders() {
		var api = new HttpRequest("", null, null, null);
		assertFalse(api.hasHeaders());
		api = new HttpRequest("", null, emptyMap(), null);
		assertFalse(api.hasHeaders());
		api = new HttpRequest("", null, Map.of("hdr1", "value1"), null);
		assertTrue(api.hasHeaders());
	}

}
