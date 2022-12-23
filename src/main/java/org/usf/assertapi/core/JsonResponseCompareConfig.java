package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseType.JSON;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class JsonResponseCompareConfig implements ResponseCompareConfig {

	private final boolean strict;
	private final String[] xpath;
	private final boolean exclude; //always true, false not 
	
	@Override
	public ResponseType getType() {
		return JSON;
	}
	
}
