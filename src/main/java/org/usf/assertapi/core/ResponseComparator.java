package org.usf.assertapi.core;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.usf.assertapi.core.TestStep.CONTENT_TYPE;
import static org.usf.assertapi.core.TestStep.HTTP_CODE;
import static org.usf.assertapi.core.TestStep.RESPONSE_CONTENT;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 *
 */
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
public class ResponseComparator {
	
	public void assumeEnabled(ApiRequest query) {
		logApiTesting(query.getExecConfig().isEnable() ? "START" : "SKIPPED");
		if(!query.getExecConfig().isEnable()) {
			throw new ApiAssertionError(true, "api assertion skipped");
		}
	}
	
	public void assertExecution(ExecutionInfo stableReleaseExec, ExecutionInfo latestReleaseExec) {
		logApiComparaison("elapsedTime", stableReleaseExec.elapsedTime() + "ms", latestReleaseExec.elapsedTime() + "ms");
		logApiComparaison("contentSize", stableReleaseExec.getSize() + "o", latestReleaseExec.getSize() + "o");
	}

	public void assertStatusCode(int expected, int actual) {
		logApiComparaison("statusCode", expected, actual);
		if(expected != actual) {
			throw notEquals(expected, actual, HTTP_CODE);
		}
	}
	
	public void assertContentType(String expected, String actual) {
		logApiComparaison("mediaType", expected, actual);
		if(!Objects.equals(expected, actual)) {
			throw notEquals(expected, actual, CONTENT_TYPE);
		}
	}

	public void assertByteContent(byte[] expected, byte[] actual) {
		logApiComparaison("byteContent", expected, actual); //just reference
		if(!Arrays.equals(expected, actual)) {
			throw notEquals(expected, actual, RESPONSE_CONTENT);
		}
	}

	public void assertTextContent(String expected, String actual) {
		logApiComparaison("textContent", expected, actual);
		if(!Objects.equals(expected, actual)) {
			throw notEquals(expected, actual, RESPONSE_CONTENT);
		}
	}
	
	public void assertJsonContent(String expected, String actual, JsonResponseCompareConfig config) {
		logApiComparaison("jsonContent", expected, actual);
		try {
			boolean strict = true;
			if(config != null) {
				expected = excludePaths(expected, config);
				actual = excludePaths(actual, config);
				strict = config.isStrict();
			}
			JSONAssert.assertEquals(expected, actual, strict);
		} catch (AssertionError e) {
			throw notEquals(expected, actual, RESPONSE_CONTENT);
		} catch (JSONException e1) {
			throw new AssertionRuntimeException(e1);
		}
	}
	
	public void assertCSVContent(String expected, String actual, CsvResponseCompareConfig config) {
		//TODO complete this
	}
	
	public void assertOK() { 
		logApiTesting("VALID");
	}

	public void assertionFail(Throwable t) {
		log.error("Testing API fail : ", t);
		throw new AssertionRuntimeException(t);
	}

	private static AssertionError notEquals(Object expected, Object actual, TestStep stage) {
		return new ApiAssertionError(false, format("%s : %s <> %s", stage, valueOf(expected), valueOf(actual))); //body size ? binary ? 
	}

	private static void logApiTesting(String msg) {
		log.info("Testing API : {}", msg);
	}
	
	private static void logApiComparaison(String stage, Object expected, Object actual) {
		log.info("Comparing API ({}) : {} <> {}", stage, expected, actual);
	}
	
    private static String excludePaths(String v, JsonResponseCompareConfig out) {
		if(out.getXpath() != null) {
			var json = JsonPath.parse(v);
			Stream.of(out.getXpath()).forEach(json::delete);
	    	v = json.jsonString();
		}
		return v;
    }
}
