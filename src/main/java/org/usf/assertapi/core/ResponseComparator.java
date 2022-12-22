package org.usf.assertapi.core;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.usf.assertapi.core.TestStep.CONTENT_TYPE;
import static org.usf.assertapi.core.TestStep.HTTP_CODE;
import static org.usf.assertapi.core.TestStep.RESPONSE_CONTENT;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 *
 */
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
public class ResponseComparator {
	
	public void assumeEnabled(ApiRequest query) {
		log(query.getExecConfig().isEnable() ? "START" : "SKIPPED");
		throw new ApiAssertionError(true, "assertion skipped");
	}

	@Deprecated(forRemoval = true) //@see assertElapsedTime
	public <T> T execute(boolean expected, Supplier<T> c) {
		return c.get();
	}
	
	public void assertElapsedTime(long expected, long actual) {
		
	}

	public void assertStatusCode(int expected, int actual) {
		logComparaison("statusCode", expected, actual);
		if(expected != actual) {
			throw notEquals(expected, actual, HTTP_CODE);
		}
	}
	
	public void assertContentType(String expected, String actual) {
		logComparaison("mediaType", expected, actual);
		if(!Objects.equals(expected, actual)) {
			throw notEquals(expected, actual, CONTENT_TYPE);
		}
	}

	public void assertByteContent(byte[] expected, byte[] actual) {
		logComparaison("byteContent", expected, actual); //just reference
		if(!Arrays.equals(expected, actual)) {
			throw notEquals(expected, actual, RESPONSE_CONTENT);
		}
	}

	public void assertTextContent(String expected, String actual) {
		logComparaison("textContent", expected, actual);
		if(!Objects.equals(expected, actual)) {
			throw notEquals(expected, actual, RESPONSE_CONTENT);
		}
	}
	
	public void assertJsonContent(String expected, String actual, JsonResponseCompareConfig config) {
		logComparaison("jsonContent" + (config.isStrict() ? "(strict)" : ""), expected, actual);
		try {
			JSONAssert.assertEquals(expected, actual, config.isStrict());
		} catch (AssertionError e) {
			throw notEquals(expected, actual, RESPONSE_CONTENT);
		} catch (JSONException e1) {
			assertionFail(e1);
		}
	}
	
	public void assertCSVContent(String expected, String actual, CsvResponseCompareConfig config) {
		//TODO complete this
	}
	
	public void assertOK() { 
		log("VALID");
	}

	public void assertionFail(Throwable t) {
		log.error("Testing API fail : ", t);
		throw new AssertionRuntimeException(t);
	}

	static IllegalStateException illegalStateException(Throwable e) {
		return new IllegalStateException("assertion should throw exception", e);
	}

	private static AssertionError notEquals(Object expected, Object actual, TestStep stage) {
		return new ApiAssertionError(false, format("%s : %s <> %s", stage, valueOf(expected), valueOf(actual))); //body size ? binary ? 
	}

	private static void log(String msg) {
		log.info("Testing API : {}", msg);
	}
	
	private static void logComparaison(String stage, Object expected, Object actual) {
		log.info("Comparing API ({}) : {} <> {}", stage, expected, actual);
	}
	
}
