package org.usf.assertapi.core;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.junit.addons.AssertExt.assertThrowsWithCause;
import static org.usf.junit.addons.AssertExt.assertThrowsWithMessage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

class SequentialFutureTest {

	@Test
	void testGet() {
		assertEquals(10, assertDoesNotThrow(()-> new SequentialFuture<>(()-> 10).get()));
		assertEquals("", assertDoesNotThrow(()-> new SequentialFuture<>(()-> "").get()));
		var exp = new IOException();
		assertThrowsWithCause(ExecutionException.class, exp, ()-> new SequentialFuture<>(()-> {throw exp;}).get());
	}

	@Test
	void testCancel() {
		assertTrue(assertDoesNotThrow(()-> new SequentialFuture<>(null).cancel(true)));
		assertTrue(assertDoesNotThrow(()-> new SequentialFuture<>(null).cancel(false)));
	}

	@Test
	void testIsCancelled() {
		assertThrowsWithMessage(UnsupportedOperationException.class, "unsupported method SequentialFuture::isCancelled", ()-> new SequentialFuture<>(null).isCancelled());
	}

	@Test
	void testIsDone() {
		assertThrowsWithMessage(UnsupportedOperationException.class, "unsupported method SequentialFuture::isDone", ()-> new SequentialFuture<>(null).isDone());
	}

	@Test
	void testGet_timeout() {
		assertThrowsWithMessage(UnsupportedOperationException.class, "unsupported method SequentialFuture::get", ()-> new SequentialFuture<>(null).get(100l, SECONDS));
	}

}
