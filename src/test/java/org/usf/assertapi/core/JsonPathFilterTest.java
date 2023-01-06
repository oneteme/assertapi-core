package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.assertapi.core.JsonContentComparator.jsonParser;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_FILTER;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyObjectException;
import org.usf.junit.addons.ConvertWithJsonParser;
import org.usf.junit.addons.FolderSource;

class JsonPathFilterTest {
	
	@Test
	void testTransformDocumentContext_xpath() {
		assertThrows(NullPointerException.class, ()-> new JsonPathFilter(null, null)); //xpaths null
		assertThrows(EmptyObjectException.class, ()-> new JsonPathFilter(null, new String[] {})); //xpaths empty
	}

	@Test
	void testJsonPathFilter_targets() {
		var jt = new JsonPathFilter(null, new String[] {""});
		assertArrayEquals(new ReleaseTarget[] {STABLE}, jt.getTargets()); //STABLE by default
//		assertTrue(jt.isExclude()); //always true
		jt = new JsonPathFilter(new ReleaseTarget[] {STABLE, LATEST}, new String[] {""});
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, jt.getTargets());
//		assertTrue(jt.isExclude()); //always true
	}
	
	@Test
	void testGetType() {
		assertEquals(JSON_PATH_FILTER.name(), new JsonPathFilter(null, new String[] {""}).getType());
	}

	@ParameterizedTest
	@FolderSource(path="json/path-filter")
	void testTransform(String origin, String expected,
			@ConvertWithJsonParser(clazz=Utils.class, method="defaultMapper") JsonPathFilter transformer) throws JSONException {
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}

}
