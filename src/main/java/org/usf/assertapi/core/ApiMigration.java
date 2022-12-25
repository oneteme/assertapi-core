package org.usf.assertapi.core;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@JsonInclude(NON_NULL)
@RequiredArgsConstructor
public final class ApiMigration implements Api {

	private final Long id;
	private final ApiRequest stableApi;
	private final ApiRequest latestApi;
	private final ResponseCompareConfig respConfig;
	
	@Override
	public ApiRequest stableApi() {
		return stableApi;
	}
	
	@Override
	public ApiRequest latestApi() {
		return latestApi;
	}
}
