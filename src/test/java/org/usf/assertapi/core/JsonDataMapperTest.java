package org.usf.assertapi.core;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

import com.jayway.jsonpath.DocumentContext;

class JsonDataMapperTest {
	
	private final String path= "$";
	private final Map<String, Object> map = Map.of("dummy", "DUMMY");
	private final DataTransformer[] transformers = new DataTransformer[] {new DataMapper(map, null)};
	
	@Test
	void testJsonDataMapper() {
		var mapper = new JsonDataMapper(null, path, transformers, null);
		assertArrayEquals(new ReleaseTarget[] {STABLE}, mapper.getApplyOn()); //STABLE by default
		assertEquals(path, mapper.path.getPath());
		assertSame(transformers, mapper.transformers);
		
		mapper = new JsonDataMapper(null, path, null, map);
		assertArrayEquals(new ReleaseTarget[] {STABLE}, mapper.getApplyOn()); //STABLE by default
		assertEquals(path, mapper.path.getPath());
		assertEquals(1, mapper.transformers.length);
		assertInstanceOf(DataMapper.class, mapper.transformers[0]);
		assertSame(map, ((DataMapper)mapper.transformers[0]).map);
	}
	
	@Test
	void testJsonDataMapper_path() {
		var msg = "JSON_DATA_MAPPER : require [path] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDataMapper(null, null, transformers, null));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDataMapper(null, null, transformers, null));
	}

	@Test
	void testJsonDataMapper_transformers() {
		var msg = "JSON_DATA_MAPPER : require [transformers] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDataMapper(null, path, null, null));
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDataMapper(null, path, new DataTransformer[]{}, null));
	}

	@Test
	void testJsonDataMapper_map() {
		var msg = "DATA_MAPPER : require [map<oldValue|regex,newValue>] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonDataMapper(null, path, null, emptyMap()));
	}

	@ParameterizedTest
	@FolderSource(path="transformer/json/data-mapper")
	void testTransform(String origin, String expected,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") ModelTransformer<DocumentContext> transformer) throws JSONException {	
		var json = jsonParser.parse(origin);
		assertInstanceOf(JsonDataMapper.class, transformer).transform(json); // test @JsonTypeName deserialization 
		JSONAssert.assertEquals(expected, transformer.transform(json).jsonString(), true);
	}
	
	@Test
	void testToString() {
		assertEquals("JSON_DATA_MAPPER(STABLE) $", new JsonDataMapper(null, path, transformers, null).toString());
	}
}
