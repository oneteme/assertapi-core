package org.usf.assertapi.core;

import static org.usf.assertapi.core.Utils.unsupportedMethod;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class SequentialFuture<T> implements Future<T> {
	
	private final Callable<T> function;
	
	@Override
	public T get() throws InterruptedException, ExecutionException {
		try {
			return function.call();
		} catch (Exception e) {
			throw new ExecutionException(e); //manage exceptions
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return true; //!important
	}

	@Override
	public boolean isCancelled() {
		throw unsupportedMethod(getClass(), "isCancelled");
	}

	@Override
	public boolean isDone() {
		throw unsupportedMethod(getClass(), "isDone");
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		throw unsupportedMethod(getClass(), "get");
	}
}
