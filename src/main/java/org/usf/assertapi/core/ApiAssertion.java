package org.usf.assertapi.core;

import lombok.NonNull;

import java.util.List;

public interface ApiAssertion {

	void assertApiAsync(@NonNull List<ApiRequest> queries, Runnable task);

	void assertApi(ApiRequest query);

}