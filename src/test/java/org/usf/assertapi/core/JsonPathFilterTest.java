package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.DataTransformer.TransformerType.JSON_PATH_FILTER;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

class JsonPathFilterTest {
	
	@Test
	void testTransformDocumentContext_xpath() {
		var msg = "JSON_PATH_FILTER : require [paths] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonPathFilter(null, null)); //xpaths null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonPathFilter(null, new String[] {})); //xpaths empty
	}

	@Test
	void testJsonPathFilter_targets() {
		var jt = new JsonPathFilter(null, new String[] {""});
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getApplyOn()); //STABLE by default
//		assertTrue(jt.isExclude()); //always true
		jt = new JsonPathFilter(new ReleaseTarget[] {STABLE, LATEST}, new String[] {""});
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getApplyOn());
//		assertTrue(jt.isExclude()); //always true
	}
	
	@Test
	void testGetType() {
		assertEquals(JSON_PATH_FILTER.name(), new JsonPathFilter(null, new String[] {""}).getType());
	}

	@ParameterizedTest
	@FolderSource(path="json/path-filter")
	void testTransform(String origin, String expected,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") JsonPathFilter transformer) throws JSONException {
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}

}
