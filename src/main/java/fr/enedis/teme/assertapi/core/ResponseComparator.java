package fr.enedis.teme.assertapi.core;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

public interface ResponseComparator {
	
	void assumeEnabled(boolean enable);
	
	ResponseEntity<byte[]> assertNotResponseException(SafeSupplier<ResponseEntity<byte[]>> supp);

	RestClientResponseException assertResponseException(SafeSupplier<?> supp);
	
	void assertStatusCode(int expectedStatusCode, int actualStatusCode);
	
	void assertContentType(MediaType expectedContentType, MediaType actualContentType);

	void assertByteContent(byte[] expectedContent, byte[] actualContent);

	void assertTextContent(String expectedContent, String actualContent);
	
	default void assertJsonContent(String expectedContent, String actualContent, boolean strict) throws JSONException {
		var jsr = new JSONCompareResult();
        if (expectedContent != actualContent) {
	        if(expectedContent == null) {
	        	jsr.fail("Expected response is null.");
	        }
	        else if(actualContent == null) {
	        	jsr.fail("Actual response is null.");
	        }
	        else {
	        	jsr = compareJSON(expectedContent, actualContent, new DefaultComparator(strict ? STRICT : LENIENT));
	        }
	    }
		assertJsonCompareResut(jsr);
	}

	void assertJsonCompareResut(JSONCompareResult res);
	
	default ResponseComparator query(HttpQuery query) { return this; }
	
	default void testOK() { }
	
	@FunctionalInterface
	public interface SafeSupplier<T> {
		
		T get() throws Exception;
	}
	
}
