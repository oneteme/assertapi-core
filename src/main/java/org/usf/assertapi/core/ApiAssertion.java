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

	void assertAll(Stream<? extends Api> queries);

	void assertAllAsync(Supplier<Stream<? extends Api>> queries);

	default void assertAll(Api... queries) {
		assertAll(Stream.of(queries));
	}
	
	default void assertAll(List<? extends Api> queries) {
		assertAll(queries.stream());
	}
}