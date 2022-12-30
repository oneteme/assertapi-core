package org.usf.assertapi.core;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.usf.assertapi.core.CompareStage.CONTENT_TYPE;
import static org.usf.assertapi.core.CompareStage.HTTP_CODE;
import static org.usf.assertapi.core.CompareStage.RESPONSE_CONTENT;
import static org.usf.assertapi.core.CompareStatus.ERROR;
import static org.usf.assertapi.core.CompareStatus.FAIL;
import static org.usf.assertapi.core.CompareStatus.OK;
import static org.usf.assertapi.core.CompareStatus.SKIP;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
public class ResponseComparator {
	
	public void prepare(ComparableApi api) {
		logApiComparaison("START <" + api + ">");
		logApiComparaison("URL ", api.stableApi().toRequestUri(), api.latestApi().toRequestUri(), false);
	}
	
	public void assumeEnabled(boolean enabled) {
		if(!enabled) {
			logApiComparaison("TEST " + SKIP);
			throw new ApiAssertionError(true, "api assertion skipped", null, null);
		}
	}
	
	public final void assertResponse(ClientResponseWrapper expect, ClientResponseWrapper actual, TypeComparatorConfig<?> config) {
		assertExecution(expect.getRequestExecution(), actual.getRequestExecution());
    	assertStatusCode(expect.getStatusCodeValue(), actual.getStatusCodeValue());
    	assertContentType(expect.getContentTypeValue(), actual.getContentTypeValue());
		if(expect.isTextCompatible()) {
	    	var eCont = expect.getResponseBodyAsString();
	    	var aCont = actual.getResponseBodyAsString();
	    	if(expect.isJsonCompatible()) {
	    		assertJsonContent(eCont, aCont, config);
	    	}
	    	else {
	    		assertTextContent(eCont, aCont);
	    	}
		}
		else {
			assertByteContent(expect.getResponseBodyAsByteArray(), actual.getResponseBodyAsByteArray());
		}
	}
	
	public void assertExecution(ExecutionInfo stableReleaseExec, ExecutionInfo latestReleaseExec) {
		logApiComparaison("elapsedTime", stableReleaseExec.elapsedTime() + "ms", latestReleaseExec.elapsedTime() + "ms", false);
		logApiComparaison("contentSize", stableReleaseExec.getSize() + "o", latestReleaseExec.getSize() + "o", false);
	}

	public void assertStatusCode(int expected, int actual) {
		logApiComparaison("statusCode", expected, actual, false);
		if(expected != actual) {
			throw failNotEqual(expected, actual, HTTP_CODE);
		}
	}
	
	public void assertContentType(String expected, String actual) {
		logApiComparaison("mediaType", expected, actual, false);
		if(!Objects.equals(expected, actual)) {
			throw failNotEqual(expected, actual, CONTENT_TYPE);
		}
	}

	public void assertByteContent(byte[] expected, byte[] actual) {
		logApiComparaison("byteContent", expected, actual, true); //just reference
		if(!Arrays.equals(expected, actual)) {
			throw failNotEqual(expected, actual, RESPONSE_CONTENT);
		}
	}

	public void assertTextContent(String expected, String actual) {
		logApiComparaison("textContent", expected, actual, true);
		if(!Objects.equals(expected, actual)) {
			throw failNotEqual(expected, actual, RESPONSE_CONTENT);
		}
	}
	
	public void assertJsonContent(String expected, String actual, TypeComparatorConfig<?> config) {
		logApiComparaison("jsonContent", expected, actual, true);
		var cr = castConfig(config, JsonComparatorConfig.class, ()-> new JsonComparatorConfig(null, null)).compare(expected, actual);
		if(expected != cr.getExpected() || actual != cr.getActual()) {
			logApiComparaison("newContent", cr.getExpected(), cr.getActual(), true);
		}
		if(!cr.isEquals()) {
			throw failNotEqual(cr.getExpected(), cr.getActual(), RESPONSE_CONTENT); //format JSON => easy-to-compare !
		}
	}
	
	public void assertCSVContent(String expected, String actual, CsvComparatorConfig config) {
		//TODO complete this
	}
	
	public void assertOK() { 
		logApiComparaison("TEST " + OK);
	}

	public ApiAssertionRuntimeException assertionFail(Throwable t) { //must not be AssertionError
		log.error("Testing API fail : ", t);
		logApiComparaison("TEST " + ERROR);
		return t instanceof ApiAssertionRuntimeException 
				? (ApiAssertionRuntimeException) t
				: new ApiAssertionRuntimeException(t);
	}

	protected AssertionError failNotEqual(Object expected, Object actual, CompareStage stage) {
		logApiComparaison("TEST " + FAIL);
		return new ApiAssertionError(false, format("%s : stable=%s ~ latest=%s", stage, valueOf(expected), valueOf(actual)), expected, actual); //body size ? binary ? 
	}

	private static void logApiComparaison(String msg) {
		log.info("================== Comparing API : {} ==================", msg);
	}
	
	private static void logApiComparaison(String stage, Object expected, Object actual, boolean multiLine) {
		if(multiLine) {
			log.info("Comparing API {} : stable={}", format("%-15s", "("+stage+")"), expected);
			log.info("Comparing API {} : latest={}", format("%-15s", "("+stage+")"), actual);
		}
		else {
			log.info("Comparing API {} : stable={} ~ latest={}", format("%-15s", "("+stage+")"), expected, actual);
		}
	}
	
	static <T extends TypeComparatorConfig<?>> T castConfig(TypeComparatorConfig<?> obj, Class<T> expectedClass, Supplier<T> orElseGet){
		if(obj == null) {
			return orElseGet.get();
		}
		if(expectedClass.isInstance(obj)) {
			return expectedClass.cast(obj);
		}
		throw new ApiAssertionRuntimeException("mismatch API configuration");
	}
}
