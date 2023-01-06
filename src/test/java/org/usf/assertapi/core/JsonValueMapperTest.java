package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.assertapi.core.JsonContentComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyObjectException;
import org.usf.junit.addons.ConvertWithJsonParser;
import org.usf.junit.addons.FolderSource;

class JsonValueMapperTest {
	
	@Test
	void testJsonValueMapper_xpath() {
		assertThrows(NullPointerException.class, ()-> new JsonValueMapper(null, null, null, Map.of("key", "value"))); //xpaths null
		assertThrows(EmptyObjectException.class, ()-> new JsonValueMapper(null, "", null, Map.of("key", "value"))); //xpaths empty
	}

	@Test
	void testJsonValueMapper_map() {
		assertThrows(NullPointerException.class, ()-> new JsonValueMapper(null, "$.path", null, null)); //map null
		assertThrows(EmptyObjectException.class, ()-> new JsonValueMapper(null, "$.path", null, emptyMap())); //map empty
	}
	
	@Test
	void testJsonValueMapper_targets() {
		var jt = new JsonValueMapper(null, "$.path", null, Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getTargets()); //STABLE by default
		jt = new JsonValueMapper(new ReleaseTarget[] {STABLE, LATEST}, "$.path", null, Map.of("key", "value"));
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getTargets());
	}

	@Test
	void testGetType() {
		assertEquals(JSON_VALUE_MAPPER.name(), new JsonValueMapper(null, "$.path", null, Map.of("key", "value")).getType());
	}

	@ParameterizedTest
	@FolderSource(path="json/value-mapper")
	void testTransform(String origin, String expected,
			@ConvertWithJsonParser(clazz=Utils.class, method="defaultMapper") JsonValueMapper transformer) throws JSONException {
		
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}
	
}
