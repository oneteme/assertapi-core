package org.usf.assertapi.core;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.usf.assertapi.core.TemporalShift.parseUnit;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import org.junit.jupiter.api.Test;

class TemporalShiftTest {

	@Test
	void testParseUnit() {
		assertSame(YEARS, parseUnit("y"));
		assertSame(MONTHS, parseUnit("m"));
		assertSame(WEEKS, parseUnit("w"));
		assertSame(DAYS, parseUnit("d"));
		assertSame(HOURS, parseUnit("h"));
		assertSame(MINUTES, parseUnit("min"));
		assertSame(SECONDS, parseUnit("s"));
		assertSame(MILLIS, parseUnit("ms"));
		assertSame(NANOS, parseUnit("ns"));
		assertThrowsWithMessage(UnsupportedOperationException.class, "unsupported unit : a", 
				()-> parseUnit("a"));
	}

}
