package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public interface ContentComparator<T> extends PolymorphicType {
	
	CompareResult compare(T expected, T actual) throws Exception;
	
	ResponseTransformer[] getTransformers();
	
	enum ResponseType {
		TXT, CSV, JSON, XML, ZIP;
	}
	
	@Getter
	@RequiredArgsConstructor
	final class CompareResult {
		private final Object expected;
		private final Object actual;
		private final boolean equals;
	}
}
