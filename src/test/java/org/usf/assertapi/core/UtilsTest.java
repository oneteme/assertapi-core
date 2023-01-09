package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.usf.assertapi.core.Utils.EmptyValueException;

class UtilsTest {

	@Test
	void testRequireNonEmptyString() {
		var exp = "dummy";
		var msg = "dummy msg";
		assertEquals(exp, assertDoesNotThrow(()-> requireNonEmpty(exp, "", "")));
		assertEquals(msg, assertThrows(EmptyValueException.class, ()-> requireNonEmpty((String)null, "", "")).getMessage());
		assertEquals(msg, assertThrows(EmptyValueException.class, ()-> requireNonEmpty("", "", "")).getMessage());
	}

	@Test
	void testRequireNonEmptyArray() {
		var exp = new Integer[] {1,2,3};
		var msg = "dummy msg";
		assertArrayEquals(exp, assertDoesNotThrow(()-> requireNonEmpty(exp, "", "")));
		assertEquals(msg, assertThrows(EmptyValueException.class, ()-> requireNonEmpty((Integer[])null, "", "")).getMessage());
		assertEquals(msg, assertThrows(EmptyValueException.class, ()-> requireNonEmpty(new Integer[0], "", "")).getMessage());
	}

	@Test
	void testRequireNonEmptyMap() {
		var exp = Map.of("dummyKey", "dummyValue");
		var msg = "dummy msg";
		assertEquals(exp, assertDoesNotThrow(()-> requireNonEmpty(exp, "", "")));
		assertEquals(msg, assertThrows(EmptyValueException.class, ()-> requireNonEmpty((Map<?,?>)null, "", "")).getMessage());
		assertEquals(msg, assertThrows(EmptyValueException.class, ()-> requireNonEmpty(Map.of(), "", "")).getMessage());
	}

}
