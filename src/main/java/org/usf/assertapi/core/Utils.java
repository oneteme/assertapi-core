package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
	
}
