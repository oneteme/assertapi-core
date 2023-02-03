package org.usf.assertapi.core;

class JsonDefaultValueMapperTest {
	
//	@Test
//	void testJsonValueMapper_xpath() {
//		var msg = "JSON_VALUE_MAPPER : require [path] field";
//		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new DataMapper(null, null, Map.of("key", "value"))); //xpaths null
//		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new DataMapper(null, "", Map.of("key", "value"))); //xpaths empty
//	}
//
//	@Test
//	void testJsonValueMapper_map() {
//		var msg = "JSON_VALUE_MAPPER : require [Map<oldValue|regex,newValue>] field";
//		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new DataMapper(null, "$.path", null)); //map null
//		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new DataMapper(null, "$.path", emptyMap())); //map empty
//	}
//	
//	@Test
//	void testJsonValueMapper_targets() {
//		var jt = new DataMapper(null, "$.path", Map.of("key", "value"));
//		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getApplyOn()); //STABLE by default
//		jt = new DataMapper(new ReleaseTarget[] {STABLE, LATEST}, "$.path", Map.of("key", "value"));
//		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getApplyOn());
//	}
//
//	@Test
//	void testGetType() {
//		assertEquals(JSON_VALUE_MAPPER.name(), new DataMapper(null, "$.path", Map.of("key", "value")).getType());
//	}
//
//	@ParameterizedTest(name = "{2}")
//	@FolderSource(path="json/value-mapper")
//	void testTransform(String origin, String expected,
//			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") DataMapper transformer) throws JSONException {
//		
//		var json = jsonParser.parse(origin);
//		transformer.transform(json);
//		JSONAssert.assertEquals(expected, json.jsonString(), true);
//	}
//	
}
