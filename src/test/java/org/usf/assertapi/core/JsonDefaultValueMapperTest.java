package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.DataTransformer.TransformerType.JSON_VALUE_MAPPER;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

class JsonDefaultValueMapperTest {
	
	@Test
	void testJsonValueMapper_xpath() {
		var msg = "JSON_VALUE_MAPPER : require [path] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDefaultValueMapper(null, null, Map.of("key", "value"))); //xpaths null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDefaultValueMapper(null, "", Map.of("key", "value"))); //xpaths empty
	}

	@Test
	void testJsonValueMapper_map() {
		var msg = "JSON_VALUE_MAPPER : require [Map<oldValue|regex,newValue>] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDefaultValueMapper(null, "$.path", null)); //map null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDefaultValueMapper(null, "$.path", emptyMap())); //map empty
	}
	
	@Test
	void testJsonValueMapper_targets() {
		var jt = new JsonDefaultValueMapper(null, "$.path", Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getApplyOn()); //STABLE by default
		jt = new JsonDefaultValueMapper(new ReleaseTarget[] {STABLE, LATEST}, "$.path", Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getApplyOn());
	}

	@Test
	void testGetType() {
		assertEquals(JSON_VALUE_MAPPER.name(), new JsonDefaultValueMapper(null, "$.path", Map.of("key", "value")).getType());
	}

	@ParameterizedTest(name = "{2}")
	@FolderSource(path="json/value-mapper")
	void testTransform(String origin, String expected,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") JsonDefaultValueMapper transformer) throws JSONException {
		
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}
	
}
