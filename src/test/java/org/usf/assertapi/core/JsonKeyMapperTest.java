package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidPathException;

class JsonKeyMapperTest {
	
	private final String path = "$";
	private final Map<String, String> map = Map.of("key", "value");
	
	@Test
	void testJsonKeyMapper_path() {
		var msg = "JSON_KEY_MAPPER : require [path] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonKeyMapper(null, null, map)); //path null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonKeyMapper(null, "", map)); //path empty
		assertThrowsExactly(InvalidPathException.class, ()-> new JsonKeyMapper(null, "[$]", map)); //invalid path
	}

	@Test
	void testJsonKeyMapper_map() {
		var msg = "JSON_KEY_MAPPER : require [Map<oldKey,newKey>] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonKeyMapper(null, path, null)); //map null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonKeyMapper(null, path, emptyMap())); //map empty
	}
	
	@Test
	void testTypeName() {
		assertEquals("JSON_KEY_MAPPER", JsonKeyMapper.class.getAnnotation(JsonTypeName.class).value());
	}

	@ParameterizedTest
	@FolderSource(path="transformer/json/key-mapper")
	void testTransform(String origin, String expected,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") ModelTransformer<DocumentContext> transformer) throws JSONException {	
		var json = jsonParser.parse(origin);
		assertInstanceOf(JsonKeyMapper.class, transformer).transform(json); // test @JsonTypeName deserialization 
		JSONAssert.assertEquals(expected, transformer.transform(json).jsonString(), true);
	}
}
