package org.usf.assertapi.core;

import static java.lang.Long.parseLong;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;
import static java.util.regex.Pattern.compile;
import static org.usf.assertapi.core.PolymorphicType.jsonTypeName;
import static org.usf.assertapi.core.Utils.requireAnyOneNonEmpty;
import static org.usf.assertapi.core.Utils.requireNonEmpty;
import static org.usf.assertapi.core.Utils.requireStringValue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TEMPORAL_SHIFT")
public final class TemporalShiftTransformer implements DataTransformer {

	private final Pattern pattern = compile("(\\d+)(min|ms|ns|y|m|d|h|s)"); //do not change order
	private final DateTimeFormatter formatter;
	private final String plus;
	private final String minus;
	
	public TemporalShiftTransformer(String pattern, String plus, String minus) {
		this.formatter = ofPattern(requireNonEmpty(pattern, jsonTypeName(this.getClass()), "pattern"));
		requireAnyOneNonEmpty(jsonTypeName(this.getClass()), "plus|minus", Utils::isEmpty, plus, minus);
		this.plus = plus;
		this.minus = minus;
	}
	
	@Override
	public Object transform(Object value) {
		Temporal temporal = value instanceof Temporal 
				? (Temporal) value 
				: from(requireStringValue(value));
		if(plus != null) {
			temporal = adjust(temporal, plus, true); 
		}
		if(minus != null) {
			temporal = adjust(temporal, minus, false); 
		}
		return formatter.format(temporal); //origin format
	}
	
	Temporal adjust(Temporal t, String s, boolean add) {
		var m = pattern.matcher(s);
		while(m.find()) {
			var v = parseLong(m.group(1));
			var u = parseUnit(m.group(2));
			t = add 
				? t.plus(v, u)
				: t.minus(v, u);
		}
		return t;
	}
	
	Temporal from(String value) {
        TemporalAccessor ta = formatter.parseBest(value,
        		Instant::from,
        		ZonedDateTime::from,
        		OffsetDateTime::from,
        		LocalDateTime::from,
        		LocalDate::from, 
        		LocalTime::from);
        if(ta instanceof Temporal) {
        	return (Temporal)ta;
        }
        throw new UnsupportedOperationException("Unsupported pattern " + value);
	}
	
	static TemporalUnit parseUnit(String unit) {
		switch (unit) {
		case "y": return YEARS;
		case "m": return MONTHS;
		case "d": return DAYS;
		case "h": return HOURS;
		case "min": return MINUTES;
		case "s": return SECONDS;
		case "ms": return MILLIS;
		case "ns": return NANOS;
		default: throw new UnsupportedOperationException("Unsupported unit " + unit);
		}
	}
}
