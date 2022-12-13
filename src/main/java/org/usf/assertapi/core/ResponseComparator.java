package org.usf.assertapi.core;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

import java.util.function.Supplier;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.springframework.http.MediaType;

public interface ResponseComparator {
	
	void assumeEnabled(ApiRequest query);

	void assertionFail(Throwable t);

	void assertStatusCode(int expectedStatusCode, int actualStatusCode);
	
	void assertContentType(MediaType expectedContentType, MediaType actualContentType);

	void assertByteContent(byte[] expectedContent, byte[] actualContent);

	void assertTextContent(String expectedContent, String actualContent);
	
	default void assertJsonContent(String expectedContent, String actualContent, boolean strict) {
		var jsr = new JSONCompareResult();
        if (expectedContent != actualContent) {
	        if(expectedContent == null) {
	        	jsr.fail("Expected response is null.");
	        }
	        else if(actualContent == null) {
	        	jsr.fail("Actual response is null.");
	        }
	        else {
	        	try {
	        		jsr = compareJSON(expectedContent, actualContent, new DefaultComparator(strict ? STRICT : LENIENT));
	        	}
	        	catch(JSONException e) {
	        		assertionFail(e);
	        		throw expectException(e);
	        	}
	        }
	    }
		assertJsonCompareResut(jsr);
	}

	void assertJsonCompareResut(JSONCompareResult res);
	
	default void assertOK() { }

	default <T> T execute(boolean expected, Supplier<T> c) {
		return c.get();
	}

	static IllegalStateException expectException(Throwable e) {
		return new IllegalStateException("assertion should throw exception", e);
	}
		
}
