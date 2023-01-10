package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.junit.addons.AssertExt.assertThrowsMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.usf.assertapi.core.Utils.EmptyValueException;

class UtilsTest {
	
	private String msg = "object : require [field] field";

	@Test
	void testSizeOf() {
		assertEquals(0, Utils.sizeOf(null));
		assertEquals(0, Utils.sizeOf(new byte[] {}));
		assertEquals(1, Utils.sizeOf(new byte[] {1}));
	}
	
	@Test
	void testIsEmpty_string() {
		assertTrue(Utils.isEmpty((String)null));
		assertTrue(Utils.isEmpty(""));
		assertFalse(Utils.isEmpty("1"));
	}
	
	@Test
	void testIsEmpty_int_array() {
		assertTrue(Utils.isEmpty((int[])null));
		assertTrue(Utils.isEmpty(new int[] {}));
		assertFalse(Utils.isEmpty(new int[] {1}));
	}

	@Test
	void testIsEmpty_string_array() {
		assertTrue(Utils.isEmpty((String[])null));
		assertTrue(Utils.isEmpty(new String[] {}));
		assertFalse(Utils.isEmpty(new String[] {"1"}));
	}
	
	@Test
	void testIsEmpty_string_list() {
		assertTrue(Utils.isEmpty((List<?>)null));
		assertTrue(Utils.isEmpty(new ArrayList<>()));
		assertFalse(Utils.isEmpty(Arrays.asList(1)));
	}
	
	@Test
	void testIsEmpty_map() {
		assertTrue(Utils.isEmpty((Map<?,?>)null));
		assertTrue(Utils.isEmpty(new HashMap<>()));
		assertFalse(Utils.isEmpty(Map.of(1, "1")));
	}
	
	@Test
	void testRequireNonEmpty_string_map() {
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty((String)null, "object", "field"));
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty("", "object", "field"));
		assertDoesNotThrow(()-> Utils.requireNonEmpty("1", null, null));
	}

	@Test
	void testRequireNonEmpty_int_array() {
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty((int[])null, "object", "field"));
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty(new int[] {}, "object", "field"));
		assertDoesNotThrow(()-> Utils.requireNonEmpty(new int[] {1}, null, null));
	}

	@Test
	void testRequireNonEmpty_string_array() {
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty((String[])null, "object", "field"));
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty(new String[]{}, "object", "field"));
		assertDoesNotThrow(()-> Utils.requireNonEmpty(new String[] {"1"}, null, null));
	}

	@Test
	void testRequireNonEmpty_map() {
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty((Map<?,?>)null, "object", "field"));
		assertThrowsMessage(msg, EmptyValueException.class, ()-> Utils.requireNonEmpty(new HashMap<>(), "object", "field"));
		assertDoesNotThrow(()-> Utils.requireNonEmpty(Map.of(1, "1"), null, null));
	}
}
