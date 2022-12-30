package org.usf.assertapi.core;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public interface ComparableApi {
	
	Long getId();

	HttpRequest stableApi();

	HttpRequest latestApi();
	
	ExecutionConfig getExecutionConfig();

	TypeComparatorConfig<?> getTypeComparatorConfig();

}
