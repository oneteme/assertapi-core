package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.usf.assertapi.core.ResponseComparator.castConfig;

import org.junit.jupiter.api.Test;

class ResponseComparatorTest {
	
	@Test
	void testPrepare() {
		var api = new ApiRequest(null, null, null, null, "api", null, null, null, null, null, null, null);
		assertDoesNotThrow(()-> new ResponseComparator().prepare(api));
	}

	@Test
	void testAssumeEnabled() {
		assertDoesNotThrow(()-> new ResponseComparator().assumeEnabled(true));
		assertThrows(ApiAssertionError.class, ()-> new ResponseComparator().assumeEnabled(false));
	}

	@Test
	void testAssertExecution() {
		var e1 = new ExecutionInfo(1000, 2000, 200, 100);
		var e2 = new ExecutionInfo(0, Long.MAX_VALUE, 500, Integer.MAX_VALUE);
		assertDoesNotThrow(()-> new ResponseComparator().assertExecution(e1, e2));
	}

	@Test
	void testAssertStatusCode() {
		assertDoesNotThrow(()-> new ResponseComparator().assertStatusCode(500, 500));
		assertThrows(ApiAssertionError.class, ()-> new ResponseComparator().assertStatusCode(400, 500));
	}

	@Test
	void testAssertContentType() {
		assertDoesNotThrow(()-> new ResponseComparator().assertContentType("application/json", "application/json"));
		assertThrows(ApiAssertionError.class, ()-> new ResponseComparator().assertContentType("application/json;charset=UTF-8", "application/json"));
	}

	@Test
	void testAssertByteContent() {
		assertDoesNotThrow(()-> new ResponseComparator().assertByteContent(new byte[] {0, 2, 4, 6, 8, 'A'}, new byte[] {0, 2, 4, 6, 8, 'A'}));
		assertThrows(ApiAssertionError.class, ()-> new ResponseComparator().assertByteContent(new byte[] {1, 2, 3, 4, 5}, new byte[] {1, 2, 'c', 4, 5}));
	}

	@Test
	void testAssertTextContent() {
		assertDoesNotThrow(()-> new ResponseComparator().assertTextContent(
				"QuotaAmount,StartDate,OwnerName,Username\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com", 
				"QuotaAmount,StartDate,OwnerName,Username\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com"));
		assertThrows(ApiAssertionError.class, ()-> new ResponseComparator().assertTextContent(
				"QuotaAmount,StartDate,OwnerName,Username\r\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com", 
				"QuotaAmount,StartDate,OwnerName,Username\n\n150000,2016-01-01,Chris Riley,trailhead9.ub20k5i9t8ou@example.com"));
	}

	@Test
	void testAssertJsonContent() {
		assertDoesNotThrow(()-> new ResponseComparator().assertJsonContent(
				"{\"name\":\"John\",\"age\":30,\"car\":null}", //minified
				"{\n\t\"name\" : \"John\",\n\t\"age\" : 30,\n\t\"car\" : null\n}", null)); //formatted
		assertThrows(ApiAssertionError.class, ()-> new ResponseComparator().assertJsonContent(
				"{\"name\":\"John\",\"age\":30,\"car\":null}", 
				"{\"name\":\"John\",\"age\":30,\"car\":\"\"}", null)); //mismatch
		assertThrows(ApiAssertionRuntimeException.class, ()-> new ResponseComparator().assertJsonContent(
				"{\"name\":\"John\",\"age\":30,\"car\":null}", 
				"{{\"name\":\"John\",\"age\":30,\"car\":\"\"}", null)); //bad json
	}
	
	@Test
	void testAssertOK() {
		assertDoesNotThrow(new ResponseComparator()::assertOK);
	}

	@Test
	void testAssertionFail() {
		var exp = new Exception();
		var act = assertDoesNotThrow(()-> new ResponseComparator().assertionFail(exp)); //not AssertionError
		assertEquals(exp, act.getCause());
		
		var exp2 = new ApiAssertionRuntimeException("");
		act = assertDoesNotThrow(()-> new ResponseComparator().assertionFail(exp2)); //not AssertionError
		assertEquals(exp2, act);
	}
	
	@Test
	void testCastConfig() {
		var exp = new JsonComparatorConfig(null, null);
		var act = assertDoesNotThrow(()-> castConfig(null, JsonComparatorConfig.class, ()-> exp));
		assertEquals(exp, act);

		act = assertDoesNotThrow(()-> castConfig(exp, JsonComparatorConfig.class, null));
		assertEquals(exp, act);
		
		assertThrows(ApiAssertionRuntimeException.class, ()-> castConfig(exp, CsvComparatorConfig.class, null));
	}
	
}