package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.usf.assertapi.core.TemporalShift.parseUnit;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TemporalShiftTest {
	
	@ParameterizedTest
	@CsvSource({
			"y,YEARS", 
			"m,MONTHS", 
			"w,WEEKS", 
			"d,DAYS", 
			"h,HOURS", 
			"min,MINUTES", 
			"s,SECONDS", 
			"ms,MILLIS", 
			"ns,NANOS"})
	void testParseUnit(String arg, ChronoUnit expected) {
		assertSame(expected, parseUnit(arg));
	}
	
	@Test
	void testParseUnit_unsupported() {
		assertThrowsWithMessage(UnsupportedOperationException.class, "unsupported unit : a", 
				()-> parseUnit("a"));
	}

}
