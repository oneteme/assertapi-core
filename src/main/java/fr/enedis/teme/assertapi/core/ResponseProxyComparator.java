package fr.enedis.teme.assertapi.core;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResponseProxyComparator implements ResponseComparator {
	
	private final ResponseComparator comparator;
	private final RestTemplate template;
	
	public void assumeEnabled(boolean enable) {
		comparator.assumeEnabled(enable);
	}
	public ResponseEntity<byte[]> assertNotResponseException(SafeSupplier<ResponseEntity<byte[]>> supp) {
		return comparator.assertNotResponseException(supp);
	}
	public RestClientResponseException assertResponseException(SafeSupplier<?> supp) {
		return comparator.assertResponseException(supp);
	}
	public void assertStatusCode(int expectedStatusCode, int actualStatusCode) {
		comparator.assertStatusCode(expectedStatusCode, actualStatusCode);
	}
	public void assertContentType(MediaType expectedContentType, MediaType actualContentType) {
		comparator.assertContentType(expectedContentType, actualContentType);
	}
	public void assertByteContent(byte[] expectedContent, byte[] actualContent) {
		comparator.assertByteContent(expectedContent, actualContent);
	}
	public void assertTextContent(String expectedContent, String actualContent) {
		comparator.assertTextContent(expectedContent, actualContent);
	}
	public void assertJsonContent(String expectedContent, String actualContent, boolean strict) throws JSONException {
		comparator.assertJsonContent(expectedContent, actualContent, strict);
	}
	public void assertJsonCompareResut(JSONCompareResult res) {
		comparator.assertJsonCompareResut(res);
	}
	
	
}
