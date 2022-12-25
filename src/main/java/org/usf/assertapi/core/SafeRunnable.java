package org.usf.assertapi.core;

@FunctionalInterface
public interface SafeRunnable {

	void run() throws Exception;
	
}
