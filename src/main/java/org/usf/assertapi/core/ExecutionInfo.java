package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 *
 */
@Getter
@RequiredArgsConstructor
public final class ExecutionInfo {
	
	//mass transfer, must be light 

	private final long start;
	private final long end;
	private final int size;

	public long elapsedTime() {
		return end - start;
	}

	@Override
	public String toString() {
		return "fetch " + size + "o in " + elapsedTime() + " ms";
	}
}