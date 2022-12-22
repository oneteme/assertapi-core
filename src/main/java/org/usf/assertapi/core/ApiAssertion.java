package org.usf.assertapi.core;

import java.util.List;
import java.util.function.Supplier;
/**
 * 
 * @author u$f
 *
 */
public interface ApiAssertion {

	void exec(ApiRequest query);

	void execAsync(List<ApiRequest> queries);

	void execAsync(Supplier<List<ApiRequest>> queries);

}