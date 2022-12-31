package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExecutionInfoTest {

	@Test
	void testElapsedTime() {
		assertEquals(1000, new ExecutionInfo(3600, 4600, 200, 5).elapsedTime());
	}

	@Test
	void testToString() {
		assertEquals("5o transferred in 1000ms", new ExecutionInfo(3600, 4600, 200, 5).toString());
	}

}
