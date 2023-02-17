package org.usf.assertapi.core;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.usf.junit.addons.ConvertWithObjectMapper;
import org.usf.junit.addons.FolderSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class HttpRequestTest {
	
	private final ObjectMapper mapper = new ObjectMapper();

	@ParameterizedTest
	@CsvSource(ignoreLeadingAndTrailingWhitespace = false, value={
	    "'',", 		  //null
	    "'',''",	  //empty
	    "'',\t",	  //whitespace
	    "api,api",    //valid
	    "api,\tapi",  //leading
	    "api,api\t"}) //trailing
	void testHttpRequest_uri(String expected, String uri) {
		assertEquals(expected,  new HttpRequest(uri, null, null, null, null).getUri());
	}
	
	@ParameterizedTest
	@CsvSource(ignoreLeadingAndTrailingWhitespace = false, value={
	    "GET,",				//null
	    "GET,''",			//empty
	    "GET,\t",	 		//whitespace
	    "GET,get",			//lowerCase
	    "PUT,\tpUt\t",		//leading & trailing
	    "POST,\tPost",		//leading
	    "DELETE,DELETE\t"}) //trailing
	void testHttpRequest_method(String expected, String method) {
		assertEquals(expected,  new HttpRequest(null, method, null, null, null).getMethod());
	}

	@Test
	void testGetFirstHeader() {
		assertNull(new HttpRequest(null, null, null, null, null).getFirstHeader("hdr1"));
		assertNull(new HttpRequest(null, null, emptyMap(), null, null).getFirstHeader("hdr1"));
		assertNull(new HttpRequest(null, null, Map.of("hdr1", emptyList()), null, null).getFirstHeader("hdr1"));
		assertEquals("value1", new HttpRequest(null, null, Map.of("hdr1", asList("value1")), null, null).getFirstHeader("hdr1"));
		assertEquals("value2", new HttpRequest(null, null, Map.of("hdr1", asList("value2", "value1")), null, null).getFirstHeader("hdr1"));
	}
	
	@ParameterizedTest
	@CsvSource({
	    "TRY   , v1/api        , [TRY] v1/api",
	    "check , v2/api/exemple, [CHECK] v2/api/exemple",
	    "Head  , v3/resource/r1, [HEAD] v3/resource/r1"})
	void testToRequestUri(String method, String uri, String expected) {
		var req = new HttpRequest(uri, method, null, null, null);
		assertEquals(expected, req.toRequestUri());
		assertEquals(expected, req.toString());
	}

	@ParameterizedTest
	@FolderSource(path="request/http")
	void testBodyAsString(
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") Map<String, Object> expectedRequest, 
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") HttpRequest originRequest) throws JsonProcessingException, JSONException {

		var body = expectedRequest.get("body");
		var bStr = body == null ? null : mapper.writeValueAsString(body); 
		assertEquals(bStr, originRequest.bodyAsString(), true);
	}
	
	@ParameterizedTest
	@FolderSource(path="request/http")
	void testSerialize(
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") Map<String, Object> expectedRequest, 
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") HttpRequest originRequest) throws JsonProcessingException {
		assertEquals(expectedRequest.get("uri"), originRequest.getUri());
		assertEquals(expectedRequest.get("method"), originRequest.getMethod());
		assertEquals(expectedRequest.get("headers"), originRequest.getHeaders());
		var body = expectedRequest.get("body") == null
				? null
				: mapper.writeValueAsBytes(expectedRequest.get("body"));
		assertArrayEquals(body, originRequest.getBody());
		assertEquals(expectedRequest.get("lazyBody"), originRequest.getLazyBody());
	}
		
	@ParameterizedTest
	@FolderSource(path="request/http")
	void testDeserialize(String requestContent,
			@ConvertWithObjectMapper(clazz=Utils.class, method="defaultMapper") HttpRequest expectedRequest) throws JsonProcessingException, JSONException {
		assertEquals(requestContent, mapper.writeValueAsString(expectedRequest), true);
	}
	
}
