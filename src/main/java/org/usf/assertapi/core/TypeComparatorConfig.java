package org.usf.assertapi.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@type")
public interface TypeComparatorConfig<T> {
	
	String getType();
	
	CompareResult compare(T expected, T actual) throws Exception;
	
	ResponseTransformer<T>[] getTransformers();
	
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
