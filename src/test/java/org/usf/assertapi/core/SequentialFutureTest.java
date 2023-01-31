package org.usf.assertapi.core;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.usf.junit.addons.AssertExt;

class SequentialFutureTest {

	@Test
	void testGet() {
		assertEquals(10, assertDoesNotThrow(()-> new SequentialFuture<>(()-> 10).get()));
		assertEquals("", assertDoesNotThrow(()-> new SequentialFuture<>(()-> "").get()));
		var exp = new IOException();
		AssertExt.assertThrowsWithCause(exp, ExecutionException.class, ()-> new SequentialFuture<>(()-> {throw exp;}).get());
	}

	@Test
	void testCancel() {
		assertTrue(assertDoesNotThrow(()-> new SequentialFuture<>(null).cancel(true)));
		assertTrue(assertDoesNotThrow(()-> new SequentialFuture<>(null).cancel(false)));
	}

	@Test
	void testIsCancelled() {
		assertThrowsWithMessage("unsupported", UnsupportedOperationException.class, ()-> new SequentialFuture<>(null).isCancelled());
	}

	@Test
	void testIsDone() {
		assertThrowsWithMessage("unsupported", UnsupportedOperationException.class, ()-> new SequentialFuture<>(null).isDone());
	}

	@Test
	void testGet_timeout() {
		assertThrowsWithMessage("unsupported", UnsupportedOperationException.class, ()-> new SequentialFuture<>(null).get(100l, SECONDS));
	}

}
