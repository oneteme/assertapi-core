package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.usf.assertapi.core.DataMapper.replaceOrMap;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

class DataMapperTest {
	
	@Test
	void testDataMapper() {
		assertDoesNotThrow(()-> new DataMapper(Map.of("[a-zA-Z]+", "value"), null));
	}
	
	@Test
	void testDataMapper_bad_map() {
		var msg = "DATA_MAPPER : require [map<oldValue|regex,newValue>] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new DataMapper(null, null)); //map null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new DataMapper(emptyMap(), null)); //map empty
		assertThrowsExactly(PatternSyntaxException.class, ()-> new DataMapper(Map.of("[]", "value"), null)); //bad regex
	}
	
	@ParameterizedTest
	@FolderSource(path="common/data-mapper")
	void testTransform(
			@ConvertWithObjectMapper List<Map<String, Object>> tests,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") DataTransformer transformer) {
		
		assertInstanceOf(DataMapper.class, transformer);
		tests.forEach(e-> 
			assertEquals(e.get("expected"), transformer.transform(e.get("value"))));
	}

	@Test
	void testReplaceOrMap() {
		assertEquals("MM", replaceOrMap("dummy", entry("\\w+mm\\w+", "MM")));
		assertEquals("duMMy", replaceOrMap("dummy", entry("(\\w+)mm(\\w+)", "$1MM$2")));
		assertEquals(123, replaceOrMap("dummy", entry("uncheked", 123)));
		assertEquals(false, replaceOrMap("dummy", entry("dummy", false)));
	}

}
