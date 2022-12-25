package org.usf.assertapi.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since
 *
 */
@Getter
@RequiredArgsConstructor
public final class ExecutionInfo {
	
	//mass transfer, must be light 
	private final long start;
	private final long end;
	private final int status;
	private final int size;

	public long elapsedTime() {
		return end - start;
	}

	@Override
	public String toString() {
		return size + "o transferred in " + elapsedTime() + " ms";
	}
}