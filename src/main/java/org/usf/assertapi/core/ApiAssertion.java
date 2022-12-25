package org.usf.assertapi.core;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public interface ApiAssertion {

	void assertAll(Stream<? extends ApiCheck> queries);

	void assertAllAsync(Supplier<Stream<? extends ApiCheck>> queries);

	default void assertAll(ApiCheck... queries) {
		assertAll(Stream.of(queries));
	}
	
	default void assertAll(List<? extends ApiCheck> queries) {
		assertAll(queries.stream());
	}
}