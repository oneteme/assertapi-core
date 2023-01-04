package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.usf.assertapi.core.ContentComparator.ResponseType.CSV;
import static org.usf.assertapi.core.ContentComparator.ResponseType.JSON;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_KEY_MAPPER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_FILTER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
	
	public static String requireNonEmpty(String s, Supplier<String> msg) {
		return requireNonEmpty(s, String::isEmpty, msg);
	}
	
	public static <T> T[] requireNonEmpty(T[] map, Supplier<String> msg) {
		return requireNonEmpty(map, arr-> arr.length == 0, msg);
	}

	public static <K,V> Map<K, V> requireNonEmpty(Map<K, V> map, Supplier<String> msg) {
		return requireNonEmpty(map, Map::isEmpty, msg);
	}

	private static <T> T requireNonEmpty(T o, Predicate<T> fn, Supplier<String> msg) {
		if(fn.test(requireNonNull(o, msg))) {
			throw new EmptyObjectException(msg.get());
		}
		return o;
	}

	@SuppressWarnings("serial")
	public static class EmptyObjectException extends RuntimeException {

		public EmptyObjectException(String message) {
			super(message);
		}
	}
	
	public static ObjectMapper defaultMapper() {
		var mapper = json().build().registerModule(new ParameterNamesModule());
		//register TypeComparatorConfig implementations
		mapper.registerSubtypes(new NamedType(JsonContentComparator.class, JSON.name()));
		mapper.registerSubtypes(new NamedType(CsvContentComparator.class, CSV.name()));
		//register ResponseTransformer implementations
		mapper.registerSubtypes(new NamedType(JsonPathFilter.class, JSON_PATH_FILTER.name()));
		mapper.registerSubtypes(new NamedType(JsonKeyMapper.class, JSON_KEY_MAPPER.name()));
		mapper.registerSubtypes(new NamedType(JsonValueMapper.class, JSON_VALUE_MAPPER.name()));
		return mapper;
	}
}
