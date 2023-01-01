package org.usf.assertapi.core;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.usf.assertapi.core.ApiAssertionError.skippedAssertionError;
import static org.usf.assertapi.core.CompareStage.CONTENT_TYPE;
import static org.usf.assertapi.core.CompareStage.ELAPSED_TIME;
import static org.usf.assertapi.core.CompareStage.HEADER_CONTENT;
import static org.usf.assertapi.core.CompareStage.HTTP_CODE;
import static org.usf.assertapi.core.CompareStage.RESPONSE_CONTENT;
import static org.usf.assertapi.core.CompareStatus.ERROR;
import static org.usf.assertapi.core.CompareStatus.FAIL;
import static org.usf.assertapi.core.CompareStatus.OK;
import static org.usf.assertapi.core.CompareStatus.SKIP;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@Slf4j(topic = "org.usf.assertapi.core.ApiAssertion")
public class ResponseComparator {
	
	CompareStage currentStage;
	
	public void prepare(ComparableApi api) {
		this.currentStage = null; //important
		logApiComparaison("START <" + api + ">");
		logApiComparaison("URL ", api.stableApi().toRequestUri(), api.latestApi().toRequestUri(), false);
	}
	
	public void assumeEnabled(boolean enabled) {
		if(!enabled) {
			logApiComparaison("TEST " + SKIP);
			throw skippedAssertionError("api assertion skipped");
		}
	}
	
	public final void assertResponse(ClientResponseWrapper expect, ClientResponseWrapper actual, TypeComparatorConfig<?> config) throws Exception {
		assertElapsedTime(expect.getRequestExecution(), actual.getRequestExecution());
    	assertStatusCode(expect.getStatusCodeValue(), actual.getStatusCodeValue());
    	assertContentType(expect.getContentTypeValue(), actual.getContentTypeValue());
    	assertHeaders(expect.getHeaders(), actual.getHeaders());
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
		finish(OK);
	}
	
	public void assertElapsedTime(ExecutionInfo stableReleaseExec, ExecutionInfo latestReleaseExec) {
		this.currentStage = ELAPSED_TIME;
		logApiComparaison("elapsedTime", stableReleaseExec.elapsedTime() + "ms", latestReleaseExec.elapsedTime() + "ms", false);
		logApiComparaison("contentSize", stableReleaseExec.getSize() + "o", latestReleaseExec.getSize() + "o", false);
	}

	public void assertStatusCode(int expected, int actual) {
		this.currentStage = HTTP_CODE;
		logApiComparaison("statusCode", expected, actual, false);
		if(expected != actual) {
			throw failNotEqual(expected, actual);
		}
	}
	
	public void assertContentType(String expected, String actual) {
		this.currentStage = CONTENT_TYPE;
		logApiComparaison("mediaType", expected, actual, false);
		if(!Objects.equals(expected, actual)) {
			throw failNotEqual(expected, actual);
		}
	}
	
	public void assertHeaders(Map<String, List<String>> expected, Map<String, List<String>> actual) {
    	this.currentStage = HEADER_CONTENT;
		//do nothing
	}

	public void assertByteContent(byte[] expected, byte[] actual) {
    	this.currentStage = RESPONSE_CONTENT;
		logApiComparaison("byteContent", expected, actual, true); //just reference
		if(!Arrays.equals(expected, actual)) {
			throw failNotEqual(expected, actual);
		}
	}

	public void assertTextContent(String expected, String actual) {
    	this.currentStage = RESPONSE_CONTENT;
		logApiComparaison("textContent", expected, actual, true);
		if(!Objects.equals(expected, actual)) {
			throw failNotEqual(expected, actual);
		}
	}
	
	public void assertJsonContent(String expected, String actual, TypeComparatorConfig<?> config) throws Exception {
    	this.currentStage = RESPONSE_CONTENT;
		logApiComparaison("jsonContent", expected, actual, true);
		var cr = castConfig(config, JsonComparatorConfig.class, ()-> new JsonComparatorConfig(null, null)).compare(expected, actual);
		if(expected != cr.getExpected() || actual != cr.getActual()) {
			logApiComparaison("newContent", cr.getExpected(), cr.getActual(), true);
		}
		if(!cr.isEquals()) {
			throw failNotEqual(cr.getExpected(), cr.getActual()); //format JSON => easy-to-compare !
		}
	}

	public void assertionFail(Throwable t) {
		log.error("Testing API fail : ", t);
		if(t instanceof AssertionError) {
			var status = t instanceof ApiAssertionError && ((ApiAssertionError)t).isSkipped() ? SKIP : FAIL;
			finish(status);
			throw (AssertionError) t;
		}
		finish(ERROR);
		if(t instanceof RuntimeException) {
			throw (RuntimeException) t;
		}
		throw new ApiAssertionRuntimeException("Error while testing api", t);
	}
	
	public void finish(CompareStatus status) { 
		logApiComparaison("TEST " + status);
	}

	protected AssertionError failNotEqual(Object expected, Object actual) {
		return new ApiAssertionError(expected, actual, 
				format("%s : stable=%s ~ latest=%s", currentStage, valueOf(expected), valueOf(actual))); //body size ? binary ? 
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
}
