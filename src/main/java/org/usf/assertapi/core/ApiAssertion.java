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

	void assertAll(Stream<? extends ComparableApi> queries);

	void assertAllAsync(Supplier<Stream<? extends ComparableApi>> queries);

	default void assertAll(ComparableApi... queries) {
		assertAll(Stream.of(queries));
	}
	
	default void assertAll(List<? extends ComparableApi> queries) {
		assertAll(queries.stream());
	}
}