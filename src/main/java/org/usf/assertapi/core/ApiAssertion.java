package org.usf.assertapi.core;

import java.util.List;
import java.util.function.Supplier;

public interface ApiAssertion {

	void assertApi(ApiRequest query);

	void assertApiAsync(List<ApiRequest> queries);

	void assertApiAsync(Supplier<List<ApiRequest>> queries);

}