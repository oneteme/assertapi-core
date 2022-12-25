package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ApiRequestTest {
	
	@Test
	void testContructor() {

		assertThrows(IllegalArgumentException.class, ()->
			new ApiRequest(null, null, null, null, null, null, null, null, null));
		
		var api = new ApiRequest(null, "", null, null, null, null, null, null, null);
		assertNull(api.getId());
		assertNull(api.getName());
		assertNull(api.getDescription());
		assertNull(api.getHeaders());
		assertNull(api.getBody());
		assertNull(api.getRespConfig());
		assertNotNull(api.getExecConfig());
		assertEquals("/", api.getUri());
		assertEquals("GET", api.getMethod());
		assertArrayEquals(new int[] {200}, api.getAcceptableStatus());

		api = new ApiRequest(1L, "v1/api", "PUT",
				Map.of("hdr1", "value1"), "api", "some description", new int[] {500}, null, null);
		api.setBody("[]");
		assertEquals(1, api.getId());
		assertEquals("api", api.getName());
		assertEquals("some description", api.getDescription());
		assertEquals(Map.of("hdr1", "value1"), api.getHeaders());
		assertEquals("[]", api.getBody());
		assertNull(api.getRespConfig());
		assertNotNull(api.getExecConfig());
		assertEquals("/v1/api", api.getUri());
		assertEquals("PUT", api.getMethod());
		assertArrayEquals( new int[] {500}, api.getAcceptableStatus());
	}

	@Test
	void testHasHeaders() {
		var api = new ApiRequest(null, "", null, null, null, null, null, null, null);
		assertFalse(api.hasHeaders());
		api = new ApiRequest(null, "", null, emptyMap(), null, null, null, null, null);
		assertFalse(api.hasHeaders());
		api = new ApiRequest(null, "", null, Map.of("hdr1", "value1"), null, null, null, null, null);
		assertTrue(api.hasHeaders());
	}

}
