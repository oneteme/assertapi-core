package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.assertapi.core.ApiAssertionError.skippedAssertionError;
import static org.usf.assertapi.core.CompareStage.CONTENT_TYPE;
import static org.usf.assertapi.core.CompareStage.ELAPSED_TIME;
import static org.usf.assertapi.core.CompareStage.HEADER_CONTENT;
import static org.usf.assertapi.core.CompareStage.HTTP_CODE;
import static org.usf.assertapi.core.CompareStage.RESPONSE_CONTENT;
import static org.usf.assertapi.core.ResponseComparator.castConfig;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.usf.junit.addons.AssertExt;

class ResponseComparatorTest {
	
	ResponseComparator comparator;
	
	public ResponseComparatorTest() {
		comparator = new ResponseComparator();
	}
	
	@Test
	void testPrepare() {
		var api = new ApiRequest(null, null, null, null, "api", null, null, null, null, null, null, null);
		assertDoesNotThrow(()-> comparator.before(api));
		expectCurrentStage(null);
	}

	@Test
	void testAssumeEnabled() {
		assertDoesNotThrow(()-> comparator.assumeEnabled(true));
		var ex = assertThrows(ApiAssertionError.class, ()-> comparator.assumeEnabled(false));
		assertTrue(ex.isSkipped()); //assertMessage
		expectCurrentStage(null);
	}

	@Test
	void testAssertElapsedTime() {
		var e1 = new ExecutionInfo(1000, 2000, 200, 100);
		var e2 = new ExecutionInfo(0, Long.MAX_VALUE, 500, Integer.MAX_VALUE);
		assertDoesNotThrow(()-> comparator.assertElapsedTime(e1, e2));
		expectCurrentStage(ELAPSED_TIME);
	}

	@Test
	void testAssertStatusCode() {
		assertDoesNotThrow(()-> comparator.assertStatusCode(500, 500));
		expectCurrentStage(HTTP_CODE);
		assertThrows(ApiAssertionError.class, ()-> comparator.assertStatusCode(400, 500));
		expectCurrentStage(HTTP_CODE);
	}

	@Test
	void testAssertContentType() {
		assertDoesNotThrow(()-> comparator.assertContentType("application/json", "application/json"));
		expectCurrentStage(CONTENT_TYPE);
		assertThrows(ApiAssertionError.class, ()-> comparator.assertContentType("application/json;charset=UTF-8", "application/json"));
		expectCurrentStage(CONTENT_TYPE);
	}

	@Test
	void testAssertHeaders() {
		assertDoesNotThrow(()-> comparator.assertHeaders(null, null));
		expectCurrentStage(HEADER_CONTENT);
	}

	@Test
	void testAssertByteContent() {
		assertDoesNotThrow(()-> comparator.assertByteContent(new byte[] {0, 2, 4, 6, 8, 'A'}, new byte[] {0, 2, 4, 6, 8, 'A'}));
		expectCurrentStage(RESPONSE_CONTENT);
		assertThrows(ApiAssertionError.class, ()-> comparator.assertByteContent(new byte[] {1, 2, 3, 4, 5}, new byte[] {1, 2, 'c', 4, 5}));
		expectCurrentStage(RESPONSE_CONTENT);
	}

	@Test
	void testAssertTextContent() {
		assertDoesNotThrow(()-> comparator.assertTextContent(
				"QuotaAmount,StartDate,OwnerName,Username\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com", 
				"QuotaAmount,StartDate,OwnerName,Username\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com"));
		expectCurrentStage(RESPONSE_CONTENT);
		assertThrows(ApiAssertionError.class, ()-> comparator.assertTextContent(
				"QuotaAmount,StartDate,OwnerName,Username\r\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com", 
				"QuotaAmount,StartDate,OwnerName,Username\n\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com"));
		expectCurrentStage(RESPONSE_CONTENT);
	}

	@Test
	void testAssertJsonContent() {
		assertDoesNotThrow(()-> comparator.assertJsonContent(
				"{\"name\":\"John\",\"age\":30,\"car\":null}", //minified
				"{\n\t\"name\" : \"John\",\n\t\"age\" : 30,\n\t\"car\" : null\n}", null)); //formatted
		expectCurrentStage(RESPONSE_CONTENT);
		assertThrows(ApiAssertionError.class, ()-> comparator.assertJsonContent(
				"{\"name\":\"John\",\"age\":30,\"car\":null}", 
				"{\"name\":\"John\",\"age\":30,\"car\":\"\"}", null)); //mismatch
		expectCurrentStage(RESPONSE_CONTENT);
		assertThrows(ApiAssertionRuntimeException.class, ()-> comparator.assertJsonContent(
				"{\"name\":\"John\",\"age\":30,\"car\":null}", 
				"{{\"name\":\"John\",\"age\":30,\"car\":\"\"}", null)); //bad json
		expectCurrentStage(RESPONSE_CONTENT);
	}

	@Test
	void testAssertionFail() {
		assertThrowsWithMessage("", AssertionError.class, ()-> comparator.assertionFail(new AssertionError("")));
		assertThrowsWithMessage("assertion fail", ApiAssertionError.class, ()-> comparator.assertionFail(new ApiAssertionError(null, null, "assertion fail")));
		assertThrowsWithMessage("skiped", ApiAssertionError.class, ()-> comparator.assertionFail(skippedAssertionError("skiped")));
		assertThrowsWithMessage("dummy msg", RuntimeException.class,  ()-> comparator.assertionFail(new RuntimeException("dummy msg")));
		assertThrowsWithMessage("Error while testing api", ApiAssertionRuntimeException.class,  ()-> comparator.assertionFail(new Exception("unkonwn")));
		
	}

	@ParameterizedTest
	@EnumSource(CompareStatus.class)
	void testFinish(CompareStatus status) {
		assertDoesNotThrow(()-> comparator.finish(status));
	}
	
	@Test
	void testCastConfig() {
		var exp = new JsonContentComparator(null, null);
		var act = assertDoesNotThrow(()-> castConfig(null, JsonContentComparator.class, ()-> exp));
		assertEquals(exp, act);
		act = assertDoesNotThrow(()-> castConfig(exp, JsonContentComparator.class, null));
		assertEquals(exp, act);
		assertThrows(ApiAssertionRuntimeException.class, ()-> castConfig(exp, CsvContentComparator.class, null));
	}
	
	private void expectCurrentStage(CompareStage stage) {
		assertEquals(stage, comparator.getCurrentStage());
		comparator.currentStage = null;
	}
}
