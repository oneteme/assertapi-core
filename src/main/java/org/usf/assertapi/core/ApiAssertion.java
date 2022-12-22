package org.usf.assertapi.core;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 
 * @author u$f
 *
 */
public interface ApiAssertion {

	void assertAll(Stream<ApiRequest> queries);

	void assertAllAsync(Supplier<Stream<ApiRequest>> queries);

	default void assertAll(ApiRequest... queries) {
		assertAll(Stream.of(queries));
	}
	
	default void assertAll(List<ApiRequest> queries) {
		assertAll(queries.stream());
	}
}