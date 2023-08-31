package org.usf.assertapi.core;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.usf.assertapi.core.ApiAssertionError.skippedAssertionError;
import static org.usf.assertapi.core.ApiAssertionError.wasSkipped;
import static org.usf.assertapi.core.ComparisonStage.CONTENT_TYPE;
import static org.usf.assertapi.core.ComparisonStage.ELAPSED_TIME;
import static org.usf.assertapi.core.ComparisonStage.HEADER_CONTENT;
import static org.usf.assertapi.core.ComparisonStage.HTTP_CODE;
import static org.usf.assertapi.core.ComparisonStage.RESPONSE_CONTENT;
import static org.usf.assertapi.core.ComparisonStatus.ERROR;
import static org.usf.assertapi.core.ComparisonStatus.FAIL;
import static org.usf.assertapi.core.ComparisonStatus.OK;
import static org.usf.assertapi.core.ComparisonStatus.SKIP;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.usf.assertapi.core.ApiExecutor.PairResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
	
	@Setter(AccessLevel.PACKAGE)
	private ApiExecutor executor;
	ComparisonStage currentStage;
	
	private static ExecutorService es;
	
	public Future<?> assertAsync(@NonNull Supplier<Stream<ApiRequest>> queries)  {
		return executor().submit(()-> assertAll(queries.get()));
	}
	
	public void assertAll(Stream<ApiRequest> stream) {
		stream.forEach(q->{
			try {
				assertApi(q);
			}
	    	catch(Exception | AssertionError e) {/* do nothing exception already logged */}
		});
	}
	
	public final void assertApi(ApiRequest api) {
		this.currentStage = null; //important : init starting stage
		try {
			before(api);
			assumeEnabled(api.getExecution().isEnabled());
			var pair = exchange(api); //
			assertElapsedTime(pair.getExpected().getRequestExecution(), pair.getActual().getRequestExecution());
	    	assertStatusCode(pair.getExpected().getStatusCodeValue(), pair.getActual().getStatusCodeValue());
	    	assertContentType(pair.getExpected().getContentTypeValue(), pair.getActual().getContentTypeValue());
	    	assertHeaders(pair.getExpected().getHeaders(), pair.getActual().getHeaders());
			if(pair.getExpected().isTextCompatible()) {
		    	var eCont = pair.getExpected().getResponseBodyAsString();
		    	var aCont = pair.getActual().getResponseBodyAsString();
		    	if(pair.getExpected().isJsonCompatible()) {
		    		assertJsonContent(eCont, aCont, api.comparator(pair.getExpected().getStatusCodeValue()));
		    	}
		    	else {
		    		assertTextContent(eCont, aCont);
		    	}
			}
			else {
				assertByteContent(pair.getExpected().getResponseBodyAsByteArray(), pair.getActual().getResponseBodyAsByteArray(), api.comparator(pair.getExpected().getStatusCodeValue()));
			}
			finish(OK);
		}
		catch (Exception | AssertionError e) {
			assertionFail(e);
		}
	}
	
	protected PairResponse exchange(ApiRequest api) {
		return executor.exchange(api); //
	}
	
	public void before(ApiRequest api) {
		logApiComparaison("START <" + api + ">");
		logApiComparaison("URL ", api.stable().toRequestUri(), api.latest().toRequestUri(), false);
	}
	
	public void assumeEnabled(boolean enabled) {
		if(!enabled) {
			throw skippedAssertionError("api assertion skipped");
		}
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
    	logApiComparaison("headers", expected, actual, true);
	}

	public void assertByteContent(byte[] expected, byte[] actual, ModelComparator<?> config) {
    	this.currentStage = RESPONSE_CONTENT;
		logApiComparaison("byteContent", expected, actual, true); //just reference
		var cr = castConfig(config, BinaryDataComparator.class, BinaryDataComparator::new).compare(expected, actual);
		if(!cr.isEquals()) {
			throw failNotEqual(cr.getExpected(), cr.getActual());
		}
	}

	public void assertTextContent(String expected, String actual) {
    	this.currentStage = RESPONSE_CONTENT;
		logApiComparaison("textContent", expected, actual, true);
		if(!Objects.equals(expected, actual)) {
			throw failNotEqual(expected, actual);
		}
	}
	
	public void assertJsonContent(String expected, String actual, ModelComparator<?> config) {
    	this.currentStage = RESPONSE_CONTENT;
		logApiComparaison("jsonContent", expected, actual, true);
		var cr = castConfig(config, JsonDataComparator.class, ()-> new JsonDataComparator(null, null)).compare(expected, actual);
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
			finish(wasSkipped(t) ? SKIP : FAIL);
			throw (AssertionError) t;
		}
		finish(ERROR);
		if(t instanceof RuntimeException) {
			throw (RuntimeException) t;
		}
		throw new ApiAssertionRuntimeException("Error while testing api", t);
	}
	
	public void finish(ComparisonStatus status) { 
		logApiComparaison("TEST " + status);
	}

	protected AssertionError failNotEqual(Object expected, Object actual) {
		return new ApiAssertionError(expected, actual, 
				format("%s : stable=%s ~ latest=%s", currentStage, valueOf(expected), valueOf(actual))); //body size ? binary ? 
	}
	
	static <T extends ModelComparator<?>> T castConfig(ModelComparator<?> obj, Class<T> expectedClass, Supplier<T> orElseGet){
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
		String format = format("%-15s", "("+stage+")");
		if(multiLine) {
			log.info("Comparing API {} : stable={}", format, expected);
			log.info("Comparing API {} : latest={}", format, actual);
		}
		else {
			log.info("Comparing API {} : stable={} ~ latest={}", format, expected, actual);
		}
	}

	static ExecutorService executor() {
		if(es == null) {
			es = newFixedThreadPool(10); //conf
		}
		return es;
	}
}
