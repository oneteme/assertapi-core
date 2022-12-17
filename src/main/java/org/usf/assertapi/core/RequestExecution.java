package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor // ??
public final class RequestExecution {
	
	private final String host;
	private long start;
	private long end;

	@Override
	public String toString() {
		return "run on " + host + " in " + (end - start) + " ms";
	}
}