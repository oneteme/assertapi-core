package org.usf.assertapi.core;

import com.fasterxml.jackson.annotation.JsonCreator;

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
	
	@JsonCreator //do not use constructor lombok annotation 
	public RequestExecution(String host, long start, long end) {
		this.host = host;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return "run on " + host + " in " + (end - start) + " ms";
	}
}