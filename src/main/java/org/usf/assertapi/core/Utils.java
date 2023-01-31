package org.usf.assertapi.core;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.usf.assertapi.core.ContentComparator.ResponseType.CSV;
import static org.usf.assertapi.core.ContentComparator.ResponseType.JSON;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_KEY_MAPPER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_FILTER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_MOVER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
				//register TypeComparatorConfig implementations
				new NamedType(JsonContentComparator.class, JSON.name())
				, new NamedType(CsvContentComparator.class, CSV.name())
				//register ResponseTransformer implementations
				, new NamedType(JsonPathFilter.class, JSON_PATH_FILTER.name())
				, new NamedType(JsonPathMover.class, JSON_PATH_MOVER.name())
				, new NamedType(JsonKeyMapper.class, JSON_KEY_MAPPER.name())
				, new NamedType(JsonRegexValueMapper.class, JSON_VALUE_MAPPER.name()));
		
	}
	
	public static ObjectMapper defaultMapper() {
		return json().build().registerModules(new ParameterNamesModule(), defaultModule());
	}
}
