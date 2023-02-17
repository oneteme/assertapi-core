package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.assertapi.core.Utils.*;
import static org.usf.assertapi.core.Utils.isEmpty;
import static org.usf.assertapi.core.Utils.requireNonEmpty;
import static org.usf.assertapi.core.Utils.requireStringValue;
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
	
	private final String msg = "object : require [name] field";
	private final String parent = "object";
	private final String field = "name";

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
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty((String)null, parent, field));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty("", parent, field));
		assertDoesNotThrow(()-> requireNonEmpty("1", null, null));
	}

	@Test
	void testRequireNonEmpty_int_array() {
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty((int[])null, parent, field));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty(new int[] {}, parent, field));
		assertDoesNotThrow(()-> requireNonEmpty(new int[] {1}, null, null));
	}

	@Test
	void testRequireNonEmpty_string_array() {
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty((String[])null, parent, field));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty(new String[]{}, parent, field));
		assertDoesNotThrow(()-> requireNonEmpty(new String[] {"1"}, null, null));
	}

	@Test
	void testRequireNonEmpty_map() {
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty((Map<?,?>)null, parent, field));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireNonEmpty(new HashMap<>(), parent, field));
		assertDoesNotThrow(()-> requireNonEmpty(Map.of(1, "1"), null, null));
	}

	@Test
	void testRequireAnyOneNonEmpty() {
		String[] ags = null;
		assertThrowsExactly(NullPointerException.class, ()-> requireAnyOneNonEmpty(parent, field, Utils::isEmpty, ags));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireAnyOneNonEmpty(parent, field, Utils::isEmpty, ""));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireAnyOneNonEmpty(parent, field, Utils::isEmpty, (String)null));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> requireAnyOneNonEmpty(parent, field, Utils::isEmpty, null, ""));
		assertDoesNotThrow(()-> requireAnyOneNonEmpty(parent, field, Utils::isEmpty, null, "dummy"));
		assertDoesNotThrow(()-> requireAnyOneNonEmpty(parent, field, Utils::isEmpty, null, "dummy", ""));
	}
	
	@Test
	void testRequireStringValue() {
		assertThrowsExactly(IllegalArgumentException.class, ()-> requireStringValue(null));
		assertThrowsExactly(IllegalArgumentException.class, ()-> requireStringValue(new Object()));
		assertThrowsExactly(IllegalArgumentException.class, ()-> requireStringValue(12345));
		assertDoesNotThrow(()-> requireStringValue(""));
		assertDoesNotThrow(()-> requireStringValue("dummy"));
	}
	
	@Test
	void testDefaultMapper() {
		var mapper = defaultMapper();
		assertTrue(mapper.getRegisteredModuleIds().stream().anyMatch("assertapi"::equals)); //AssertapiModule module loaded
		assertTrue(mapper.getRegisteredModuleIds().stream().anyMatch("jackson-module-parameter-names"::equals)); //ParameterNamesModule module loaded
	}
	
}
