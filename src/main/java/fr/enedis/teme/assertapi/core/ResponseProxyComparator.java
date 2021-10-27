package fr.enedis.teme.assertapi.core;

import static fr.enedis.teme.assertapi.core.ApiAssertionsResult.TestStatus.KO;
import static fr.enedis.teme.assertapi.core.ApiAssertionsResult.TestStatus.OK;
import static fr.enedis.teme.assertapi.core.ApiAssertionsResult.TestStatus.SKIP;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import fr.enedis.teme.assertapi.core.ApiAssertionsResult.TestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResponseProxyComparator implements ResponseComparator {
	
	private final ResponseComparator comparator;
	private final Consumer<ApiAssertionsResult> tracer;
	private final ServerConfig exServerConfig;
	private final ServerConfig acServerConfig;
	private final HttpQuery query;

	public ResponseProxyComparator(ResponseComparator comparator, Consumer<ApiAssertionsResult> tracer, ServerConfig exServerConfig, ServerConfig acServerConfig) {
		this.comparator = comparator;
		this.tracer = tracer;
		this.exServerConfig = exServerConfig;
		this.acServerConfig = acServerConfig;
		this.query = null;
	}
	
	public void assumeEnabled(boolean enable) {
		try {
			comparator.assumeEnabled(enable);
		}
		catch(Throwable e) {
			trace(SKIP);
			throw e;
		}
	}

	public ResponseEntity<byte[]> assertNotResponseException(SafeSupplier<ResponseEntity<byte[]>> supp) {
		try {
			return comparator.assertNotResponseException(supp);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
	}
	public RestClientResponseException assertResponseException(SafeSupplier<?> supp) {
		try {
			return comparator.assertResponseException(supp);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
	}
	public void assertStatusCode(int expectedStatusCode, int actualStatusCode) {
		try {
			comparator.assertStatusCode(expectedStatusCode, actualStatusCode);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
	}
	public void assertContentType(MediaType expectedContentType, MediaType actualContentType) {
		try {
			comparator.assertContentType(expectedContentType, actualContentType);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
		
	}
	public void assertByteContent(byte[] expectedContent, byte[] actualContent) {
		try {
			comparator.assertByteContent(expectedContent, actualContent);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
	}
	public void assertTextContent(String expectedContent, String actualContent) {
		try {
			comparator.assertTextContent(expectedContent, actualContent);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
	}
	public void assertJsonContent(String expectedContent, String actualContent, boolean strict) throws JSONException {
		try {
			comparator.assertJsonContent(expectedContent, actualContent, strict);
		}
		catch(Throwable e) {
			trace(KO);
			throw e;
		}
	}
	
	public void assertJsonCompareResut(JSONCompareResult res) {
		//should not be call
	}
	
	@Override
	public ResponseComparator query(HttpQuery query) {
		return new ResponseProxyComparator(comparator, tracer, exServerConfig, acServerConfig, requireNonNull(query));
	}

	@Override
	public void testOK() { 
		try {
			comparator.testOK();
			trace(OK);
		}
		catch(Exception e) {
			trace(KO);
		}
	}
	
	private void trace(TestStatus status) {
		try {
			tracer.accept(new ApiAssertionsResult(
					exServerConfig.buildRootUrl(),
					acServerConfig.buildRootUrl(),
					query,
					status
				));
		}
		catch(Exception e) {
			log.warn("cannot trace this test : {}", e.getMessage());
		}
	}
}
