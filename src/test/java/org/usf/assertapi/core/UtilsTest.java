package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.usf.assertapi.core.Utils.EmptyObjectException;

class UtilsTest {

	@Test
	void testRequireNonEmptyString() {
		var exp = "dummy";
		var msg = "dummy msg";
		assertEquals(exp, assertDoesNotThrow(()-> requireNonEmpty(exp, null)));
		assertEquals(msg, assertThrows(NullPointerException.class, ()-> requireNonEmpty((String)null, ()-> msg)).getMessage());
		assertEquals(msg, assertThrows(EmptyObjectException.class, ()-> requireNonEmpty("", ()-> msg)).getMessage());
	}

	@Test
	void testRequireNonEmptyArray() {
		var exp = new Integer[] {1,2,3};
		var msg = "dummy msg";
		assertArrayEquals(exp, assertDoesNotThrow(()-> requireNonEmpty(exp, null)));
		assertEquals(msg, assertThrows(NullPointerException.class, ()-> requireNonEmpty((Integer[])null, ()-> msg)).getMessage());
		assertEquals(msg, assertThrows(EmptyObjectException.class, ()-> requireNonEmpty(new Integer[0], ()-> msg)).getMessage());
	}

	@Test
	void testRequireNonEmptyMap() {
		var exp = Map.of("dummyKey", "dummyValue");
		var msg = "dummy msg";
		assertEquals(exp, assertDoesNotThrow(()-> requireNonEmpty(exp, null)));
		assertEquals(msg, assertThrows(NullPointerException.class, ()-> requireNonEmpty((Map<?,?>)null, ()-> msg)).getMessage());
		assertEquals(msg, assertThrows(EmptyObjectException.class, ()-> requireNonEmpty(Map.of(), ()-> msg)).getMessage());
	}

}
