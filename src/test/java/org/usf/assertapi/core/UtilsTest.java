package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.assertapi.core.Utils.defaultMapper;
import static org.usf.assertapi.core.Utils.isEmpty;
import static org.usf.assertapi.core.Utils.requireNonEmpty;
import static org.usf.assertapi.core.Utils.sizeOf;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.usf.assertapi.core.Utils.EmptyValueException;

class UtilsTest {
	
	private String msg = "object : require [name] field";

	@Test
	void testSizeOf() {
		assertEquals(0, sizeOf(null));
		assertEquals(0, sizeOf(new byte[] {}));
		assertEquals(1, sizeOf(new byte[] {1}));
	}
	
	@Test
	void testIsEmpty_string() {
		assertTrue(isEmpty((String)null));
		assertTrue(isEmpty(""));
		assertFalse(isEmpty("1"));
	}
	
	@Test
	void testIsEmpty_int_array() {
		assertTrue(isEmpty((int[])null));
		assertTrue(isEmpty(new int[] {}));
		assertFalse(isEmpty(new int[] {1}));
	}

	@Test
	void testIsEmpty_string_array() {
		assertTrue(isEmpty((String[])null));
		assertTrue(isEmpty(new String[] {}));
		assertFalse(isEmpty(new String[] {"1"}));
	}
	
	@Test
	void testIsEmpty_string_list() {
		assertTrue(isEmpty((List<?>)null));
		assertTrue(isEmpty(new ArrayList<>()));
		assertFalse(isEmpty(Arrays.asList(1)));
	}
	
	@Test
	void testIsEmpty_map() {
		assertTrue(isEmpty((Map<?,?>)null));
		assertTrue(isEmpty(new HashMap<>()));
		assertFalse(isEmpty(Map.of(1, "1")));
	}
	
	@Test
	void testRequireNonEmpty_string_map() {
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty((String)null, "object", "name"));
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty("", "object", "name"));
		assertDoesNotThrow(()-> requireNonEmpty("1", null, null));
	}

	@Test
	void testRequireNonEmpty_int_array() {
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty((int[])null, "object", "name"));
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty(new int[] {}, "object", "name"));
		assertDoesNotThrow(()-> requireNonEmpty(new int[] {1}, null, null));
	}

	@Test
	void testRequireNonEmpty_string_array() {
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty((String[])null, "object", "name"));
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty(new String[]{}, "object", "name"));
		assertDoesNotThrow(()-> requireNonEmpty(new String[] {"1"}, null, null));
	}

	@Test
	void testRequireNonEmpty_map() {
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty((Map<?,?>)null, "object", "name"));
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> requireNonEmpty(new HashMap<>(), "object", "name"));
		assertDoesNotThrow(()-> requireNonEmpty(Map.of(1, "1"), null, null));
	}

	@Test
	void testDefaultMapper() {
		var mapper = defaultMapper();
		assertTrue(mapper.getRegisteredModuleIds().stream().anyMatch("assertapi"::equals)); //AssertapiModule module loaded
		assertTrue(mapper.getRegisteredModuleIds().stream().anyMatch("jackson-module-parameter-names"::equals)); //ParameterNamesModule module loaded
	}
	
}
