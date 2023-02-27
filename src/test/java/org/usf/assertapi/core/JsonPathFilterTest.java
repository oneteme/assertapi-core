package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.usf.assertapi.core.Utils.EmptyValueException;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidPathException;

class JsonPathFilterTest {

	@Test
	void testTransformDocumentContext_path() {
		var msg = "JSON_PATH_FILTER : require [paths] field";
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonPathFilter(null, null)); //paths null
		assertThrowsWithMessage(EmptyValueException.class, msg, ()-> new JsonPathFilter(null, new String[] {})); //paths empty
		assertThrowsExactly(InvalidPathException.class, ()-> new JsonPathFilter(null, new String[] {"[$]"})); //paths empty
	}

	@Test
	void testTypeName() {
		assertEquals("JSON_PATH_FILTER", JsonPathFilter.class.getAnnotation(JsonTypeName.class).value());
	}

	@ParameterizedTest
	@FolderSource(path="transformer/json/path-filter")
	void testTransform(String origin, String expected,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") ModelTransformer<DocumentContext> transformer) throws JSONException {
		var json = jsonParser.parse(origin);
		assertInstanceOf(JsonPathFilter.class, transformer); // test @JsonTypeName deserialization 
		JSONAssert.assertEquals(expected, transformer.transform(json).jsonString(), true);
	}

}
