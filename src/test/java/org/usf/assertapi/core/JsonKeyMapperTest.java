package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.assertapi.core.JsonContentComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_KEY_MAPPER;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithJsonParser;
import org.usf.junit.addons.FolderSource;

class JsonKeyMapperTest {
	
	@Test
	void testJsonKeyMapper_xpath() {
		var msg = "JSON_KEY_MAPPER : require [xpath] field";
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonKeyMapper(null, null, Map.of("key", "value"))); //xpaths null
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonKeyMapper(null, "", Map.of("key", "value"))); //xpaths empty
	}

	@Test
	void testJsonKeyMapper_map() {
		var msg = "JSON_KEY_MAPPER : require [Map<oldKey,newKey>] field";
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonKeyMapper(null, "$.path", null)); //map null
		assertThrowsWithMessage(msg, EmptyValueException.class, ()-> new JsonKeyMapper(null, "$.path", emptyMap())); //map empty
	}

	@Test
	void testJsonKeyMapper_targets() {
		var jt = new JsonKeyMapper(null, "$.path", Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getTargets()); //STABLE by default
		jt = new JsonKeyMapper(new ReleaseTarget[] {STABLE, LATEST}, "$.path", Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getTargets());
	}
	
	@Test
	void testGetType() {
		assertEquals(JSON_KEY_MAPPER.name(), new JsonKeyMapper(null, "$.path", Map.of("key", "value")).getType());
	}

	@ParameterizedTest
	@FolderSource(path="json/key-mapper")
	void testTransform(String origin, String expected,
			@ConvertWithJsonParser(clazz=Utils.class, method="defaultMapper") JsonKeyMapper transformer) throws JSONException {
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}
}
