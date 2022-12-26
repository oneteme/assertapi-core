package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExecutionConfigTest {

	@Test
	void testContructor() {
		var ee = new ExecutionConfig(null, null);
		assertTrue(ee.isEnabled());
		assertTrue(ee.isParallel());

		ee = new ExecutionConfig(false, false);
		assertFalse(ee.isEnabled());
		assertFalse(ee.isParallel());

		ee = new ExecutionConfig(false, true);
		assertFalse(ee.isEnabled());
		assertTrue(ee.isParallel());
	}

}
