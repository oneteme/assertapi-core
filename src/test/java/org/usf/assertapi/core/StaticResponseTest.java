package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

class StaticResponseTest {

	@Test
	void testStaticResponse_Status() {
		assertEquals(200, new StaticResponse(null, null, null, null).getStatus());
		assertEquals(404, new StaticResponse(404, null, null, null).getStatus());
	}
	
	@Test
	void testWithBody() {
		var status = Integer.valueOf(404);
		var haders = Map.of("key", Arrays.asList("value"));
		var lasyBd = "filename.json";
		var sr = new StaticResponse(status, haders, new byte[]{}, lasyBd)
				.withBody(new byte[] {1,2,3});
		assertEquals(status, sr.getStatus()); //primitive status type
		assertSame(haders, sr.getHeaders());
		assertSame(lasyBd, sr.getLazyBody());
		assertArrayEquals(new byte[] {1,2,3}, sr.getBody());
	}
	
	@Test
	void testGetUri() {
		assertThrowsExactly(UnsupportedOperationException.class, ()-> new StaticResponse(null, null, null, null).getUri());
	}
	
	@Test
	void testGetMethod() {
		assertThrowsExactly(UnsupportedOperationException.class, ()-> new StaticResponse(null, null, null, null).getMethod());
	}

}
