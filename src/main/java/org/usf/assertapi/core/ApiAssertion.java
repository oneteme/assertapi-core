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

	void assertApi(ComparableApi api);

	void assertAllAsync(Supplier<Stream<? extends ComparableApi>> stream);
	
	default void assertAll(List<? extends ComparableApi> list) {
		assertAll(list.stream());
	}

	default void assertAll(Stream<? extends ComparableApi> stream) {
		stream.forEach(q->{
			try {
				assertApi(q);
			}
	    	catch(Throwable e) {/* do nothing */} //Exception + Error
		});
	}
}