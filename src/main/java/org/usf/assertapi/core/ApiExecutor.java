package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface ApiExecutor {

	PairResponse exchange(ApiRequest api);
	
	@Getter
	@RequiredArgsConstructor
	final class PairResponse {
		
		private final ClientResponseWrapper expected;
		private final ClientResponseWrapper actual;
	}

}