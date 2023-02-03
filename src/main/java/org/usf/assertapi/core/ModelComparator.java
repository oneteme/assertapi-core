package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@FunctionalInterface
public interface ModelComparator<T> extends PolymorphicType {
	
	CompareResult compare(T expected, T actual);
	
	@Getter
	@RequiredArgsConstructor
	final class CompareResult {
		private final Object expected;
		private final Object actual;
		private final boolean equals;
	}
}
