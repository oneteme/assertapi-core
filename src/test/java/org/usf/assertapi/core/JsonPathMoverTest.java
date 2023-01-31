package org.usf.assertapi.core;

import static org.usf.assertapi.core.JsonContentComparator.jsonParser;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.junit.addons.ConvertWithJsonParser;
import org.usf.junit.addons.FolderSource;

class JsonPathMoverTest {

	@Test
	void testJsonPathMover() {
//		fail("Not yet implemented");
	}

	@Test
	void testGetType() {
//		fail("Not yet implemented");
	}
	

	@ParameterizedTest
	@FolderSource(path="json/path-mover")
	void testTransform(String origin, String expected,
			@ConvertWithJsonParser(clazz=Utils.class, method="defaultMapper") JsonPathMover transformer) throws JSONException {
		var json = jsonParser.parse(origin);
		transformer.transform(json);
		JSONAssert.assertEquals(expected, json.jsonString(), true);
	}

}
