package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
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
		assertEquals(200, api.getReferStatus());

		api = new ApiRequest(1L, "v1/api", "PUT",
				Map.of("hdr1", "value1"), "api", "some description", 500, null, null);
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
		assertEquals(500, api.getReferStatus());
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
	

	private ApiRequest ar1 = new ApiRequest(null, "", null, null, null, null, null, null, null);
}
