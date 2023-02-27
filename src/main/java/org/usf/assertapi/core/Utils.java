package org.usf.assertapi.core;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minidev.json.JSONArray;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
	
	public static int sizeOf(byte[] arr) {//arr is nullable
		return ofNullable(arr).map(a-> a.length).orElse(0);
	}
	
	public static boolean isEmpty(String str) {
		return isNull(str) || str.isEmpty();
	}
	
	public static boolean isEmpty(int[] arr) {
		return isNull(arr) || arr.length == 0;
	}
	
	public static <T> boolean isEmpty(T[] arr) {
		return isNull(arr) || arr.length == 0;
	}
	
	public static boolean isEmpty(Collection<?> c) {
		return isNull(c) || c.isEmpty();
	}
	
	public static boolean isEmpty(Map<?, ?> map) {
		return isNull(map) || map.isEmpty();
	}
	
	public static String requireNonEmpty(String str, String parent, String fieldName) {
		return requireNonEmpty(str, Utils::isEmpty, parent, fieldName);
	}
	
	public static int[] requireNonEmpty(int[] arr, String parent, String fieldName) {
		return requireNonEmpty(arr, Utils::isEmpty, parent, fieldName);
	}
	
	public static <T> T[] requireNonEmpty(T[] arr, String parent, String fieldName) {
		return requireNonEmpty(arr, Utils::isEmpty, parent, fieldName);
	}

	public static <K,V> Map<K, V> requireNonEmpty(Map<K, V> map, String parent, String fieldName) {
		return requireNonEmpty(map, Utils::isEmpty, parent, fieldName);
	}

	private static <T> T requireNonEmpty(T o, Predicate<T> emptyFn, String parent, String fieldName) {
		if(emptyFn.test(o)) {
			throw new EmptyValueException(parent, fieldName);
		}
		return o;
	}

	@SafeVarargs
	public static <T> void requireAnyOneNonEmpty(String parent, String fieldName, Predicate<T> emptyFn, @NonNull T... args) {
		if(Stream.of(args).allMatch(emptyFn)) {
			throw new EmptyValueException(parent, fieldName);
		}
	}

	@SuppressWarnings("serial")
	public static final class EmptyValueException extends RuntimeException {

		public EmptyValueException(String parent, String fieldName) {
			super(parent + " : require [" + fieldName + "] field");
		}
	}
	
	@SuppressWarnings("serial")
	public static final class TooManyValueException extends RuntimeException {

		public TooManyValueException(String parent, String fieldName) {
			super(parent + " : [" + fieldName + "] should take only one value");
		}
	}
	
	public static SimpleModule defaultModule() {
		return new SimpleModule("assertapi").registerSubtypes(
				//register DataTransformer implementations
				DataMapper.class
				, TemporalShift.class
				//register TypeComparatorConfig implementations
				, JsonDataComparator.class
				, CsvDataComparator.class
				//register ModelTransformer implementations
				, JsonPathFilter.class
				, JsonPathMover.class
				, JsonDataMapper.class
				, JsonKeyMapper.class);
	}
	
	public static ObjectMapper defaultMapper() {
		return json().build().registerModules(new ParameterNamesModule(), defaultModule());
	}

	public static boolean isJsonObject(Object o) {
		return o instanceof Map;
	}

	public static boolean isJsonArray(Object o) {
		return o instanceof JSONArray;
	}
	
	public static String requireStringValue(Object o) {
		if(o instanceof String) {
			return o.toString();
		}
		throw new IllegalArgumentException("String value expected but was : " + o); 
	}
	
	@SafeVarargs
	static <T, F> T flow(T v, BiFunction<F, T, T> fn , F... arr) { //Stream::reduce 
		for(var f : requireNonNull(arr)) {
			v = fn.apply(f, v);
		}
		return v;
	}
	
	public static UnsupportedOperationException notImplemented() {
		return new UnsupportedOperationException("not yet implemented");
	}

	public static UnsupportedOperationException unsupportedMethod(Class<?> op, String method) {
		return new UnsupportedOperationException("unsupported method " + op.getSimpleName() + "::" + method);
	}
	
	public static UnsupportedOperationException unsupportedOperation(String op, String value) {
		return new UnsupportedOperationException("unupported " + op + " : " + value);
	}
}
