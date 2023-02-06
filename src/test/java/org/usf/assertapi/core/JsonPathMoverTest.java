package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;
import org.usf.junit.addons.ThrowableMessage;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;

class JsonPathMoverTest {

	@Test
	void testJsonPathMover() {
//		fail("Not yet implemented");
	}

	@Test
	void testTypeName() {
		assertEquals("JSON_PATH_MOVER", JsonPathMover.class.getAnnotation(JsonTypeName.class).value());
	}

	@ParameterizedTest
	@FolderSource(path="json/path-mover")
	void testTransform(String origin, String expected,
			@ConvertWithObjectMapper ThrowableMessage exception,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") ModelTransformer<DocumentContext> transformer) throws JSONException {
		var json = jsonParser.parse(origin);
		if(exception == null) {
			assertInstanceOf(JsonPathMover.class, transformer); // test @JsonTypeName deserialization 
			JSONAssert.assertEquals(expected, transformer.transform(json).jsonString(), true);
		}
		else {
			assertThrowsWithMessage(exception, ()-> transformer.transform(json));
		}
	}

}
