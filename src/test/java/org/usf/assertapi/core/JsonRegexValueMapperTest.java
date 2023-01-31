package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.assertapi.core.JsonContentComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithJsonParser;
import org.usf.junit.addons.FolderSource;

class JsonRegexValueMapperTest {
	
	@Test
	void testJsonValueMapper_xpath() {
		var msg = "JSON_VALUE_MAPPER : require [xpath] field";
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonRegexValueMapper(null, null, null, Map.of("key", "value"))); //xpaths null
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonRegexValueMapper(null, "", null, Map.of("key", "value"))); //xpaths empty
	}

	@Test
	void testJsonValueMapper_map() {
		var msg = "JSON_VALUE_MAPPER : require [Map<oldValue,newValue>] field";
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonRegexValueMapper(null, "$.path", null, null)); //map null
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonRegexValueMapper(null, "$.path", null, emptyMap())); //map empty
	}
	
	@Test
	void testJsonValueMapper_targets() {
		var jt = new JsonRegexValueMapper(null, "$.path", null, Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getTargets()); //STABLE by default
		jt = new JsonRegexValueMapper(new ReleaseTarget[] {STABLE, LATEST}, "$.path", null, Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getTargets());
	}

	@Test
	void testGetType() {
		assertEquals(JSON_VALUE_MAPPER.name(), new JsonRegexValueMapper(null, "$.path", null, Map.of("key", "value")).getType());
	}

	@ParameterizedTest
	@FolderSource(path="json/value-mapper")
	void testTransform(String origin, String expected,
			@ConvertWithJsonParser(clazz=Utils.class, method="defaultMapper") JsonRegexValueMapper transformer) throws JSONException {
		
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}
	
}
