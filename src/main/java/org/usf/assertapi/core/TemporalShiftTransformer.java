package org.usf.assertapi.core;

import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public final class TemporalShiftTransformer implements DataTransformer {

	private final Pattern pattern = compile("(\\d+)(min|ms|ns|y|m|d|h|s)");
	private final DateTimeFormatter formatter;
	private final String plus;
	private final String minus;
	
	public TemporalShiftTransformer(String pattern, String plus, String minus) {
		this.formatter = ofPattern(pattern);
		this.plus = plus;
		this.minus = minus;
		if(plus == null && minus == null) {
			throw new IllegalArgumentException("plus & minus are null"); //TODO right exp
		}
	}
	
	@Override
	public Object transform(Object value) {
		Temporal temporal = temporal(valueOf(value)); //require String
		if(plus != null) {
			temporal = adjust(temporal, plus, temporal::plus); 
		}
		if(minus != null) {
			temporal = adjust(temporal, minus, temporal::minus); 
		}
		return temporal;
	}
	
	Temporal adjust(Temporal t, String s, BiFunction<Long, TemporalUnit, Temporal> fn) {
		var m = pattern.matcher(s);
		while(m.find()) {
			t = fn.apply(parseLong(m.group(1)), unit(m.group(2)));
		}
		return t;
	}
	
	Temporal temporal(String value) {
        TemporalAccessor ta = formatter.parseBest(value, 
        		LocalTime::from,
        		LocalDate::from, 
        		LocalDateTime::from, 
        		Instant::from, 
        		ZonedDateTime::from);
        if(ta instanceof Temporal) {
        	return (Temporal)ta;
        }
        throw new IllegalArgumentException("cannot parse " + value);
	}
	
	TemporalUnit unit(String unit) {
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
	
	//TODO delete after
	public static void main(String[] args) {
		System.out.println(new TemporalShiftTransformer("yyyy-MM-dd", "6d", "1y").transform("2024-02-06"));
		System.out.println(new TemporalShiftTransformer("HH:mm", "6h", "1min").transform("06:01"));
	}
}
