package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExecutionConfigTest {

	@Test
	void testContructor() {
		var ee = new ExecutionConfig(null, null);
		assertTrue(ee.isEnable());
		assertTrue(ee.isParallel());

		ee = new ExecutionConfig(false, false);
		assertFalse(ee.isEnable());
		assertFalse(ee.isParallel());
	}

}
