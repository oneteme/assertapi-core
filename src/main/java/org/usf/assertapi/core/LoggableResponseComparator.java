package org.usf.assertapi.core;

import java.util.function.Supplier;

import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
@RequiredArgsConstructor
public final class LoggableResponseComparator implements ResponseComparator {

	private final ResponseComparator comparator;

	public void assumeEnabled(ApiRequest query) {
		if(query.getConfiguration().isEnable()) {
			log.info("API test run {}", query);
		}
		else {
			log.info("API test skip {}", query);
		}
		comparator.assumeEnabled(query);
	}

	public void assertionFail(Throwable t) {
		log.error("API test fail", t);
		comparator.assertionFail(t);
	}

	public void assertStatusCode(int expectedStatusCode, int actualStatusCode) {
		logComparaison("statusCode", expectedStatusCode, actualStatusCode);
		comparator.assertStatusCode(expectedStatusCode, actualStatusCode);
	}

	public void assertContentType(MediaType expectedContentType, MediaType actualContentType) {
		logComparaison("mediaType", expectedContentType, actualContentType);
		comparator.assertContentType(expectedContentType, actualContentType);
	}

	public void assertByteContent(byte[] expectedContent, byte[] actualContent) {
		logComparaison("byteContent", expectedContent, actualContent); //just reference
		comparator.assertByteContent(expectedContent, actualContent);
	}

	public void assertTextContent(String expectedContent, String actualContent) {
		logComparaison("textContent", expectedContent, actualContent);
		comparator.assertTextContent(expectedContent, actualContent);
	}

	public void assertJsonContent(String expectedContent, String actualContent, boolean strict) {
		logComparaison("jsonContent" + (strict ? "(strict)" : ""), expectedContent, actualContent);
		comparator.assertJsonContent(expectedContent, actualContent, strict);
	}

	public void assertJsonCompareResut(JSONCompareResult res) {
		//should not be call
	}

	public void assertOK() {
		log.info("API test OK");
		comparator.assertOK();
	}

	public <T> T execute(boolean expected, Supplier<T> c) {
		return comparator.execute(expected, c);
	}

	private static void logComparaison(String stage, Object expected, Object actual) {
		log.info("Comparing {} : {} <> {}", stage, expected, actual);
	}
	
}
