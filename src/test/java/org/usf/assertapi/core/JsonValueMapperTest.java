package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.usf.assertapi.core.Module.defaultJsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyObjectException;
import org.usf.assertapi.core.junit.FolderSource;
import org.usf.assertapi.core.junit.JsonObjectMapper;

class JsonValueMapperTest {
	
	@Test
	void testJsonValueMapper_xpath() {
		assertThrows(NullPointerException.class, ()-> new JsonValueMapper(null, null, Map.of("key", "value"))); //xpaths null
		assertThrows(EmptyObjectException.class, ()-> new JsonValueMapper(null, "", Map.of("key", "value"))); //xpaths empty
	}

	@Test
	void testJsonValueMapper_map() {
		assertThrows(NullPointerException.class, ()-> new JsonValueMapper(null, "$.path", null)); //map null
		assertThrows(EmptyObjectException.class, ()-> new JsonValueMapper(null, "$.path", emptyMap())); //map empty
	}
	
	@Test
	void testJsonValueMapper_targets() {
		var jt = new JsonValueMapper(null, "$.path", Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getTargets()); //STABLE by default
		jt = new JsonValueMapper(new ReleaseTarget[] {STABLE, LATEST}, "$.path", Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getTargets());
	}

	@Test
	void testGetType() {
		assertEquals(JSON_VALUE_MAPPER.name(), new JsonValueMapper(null, "$.path", Map.of("key", "value")).getType());
	}

	@ParameterizedTest
	@FolderSource(path="json/value-mapper")
	void testTransform(@ConvertWith(JsonObjectMapper.class) JsonValueMapper transformer, String origin, String expected) throws JSONException {
		var json = defaultJsonParser().parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}
}
