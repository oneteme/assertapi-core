package fr.enedis.teme.assertapi.core;

import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResponseProxyComparator implements ResponseComparator {
	
	private final ResponseComparator comparator;

	@Override
	public void assumeEnabled(boolean enable) {
		
	}

	@Override
	public ResponseEntity<byte[]> assertNotResponseException(SafeSupplier<ResponseEntity<byte[]>> supp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestClientResponseException assertResponseException(SafeSupplier<?> supp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assertStatusCode(int expectedStatusCode, int actualStatusCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assertContentType(MediaType expectedContentType, MediaType actualContentType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assertByteContent(byte[] expectedContent, byte[] actualContent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assertTextContent(String expectedContent, String actualContent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assertJsonCompareResut(JSONCompareResult res) {
		// TODO Auto-generated method stub
		
	}

}
