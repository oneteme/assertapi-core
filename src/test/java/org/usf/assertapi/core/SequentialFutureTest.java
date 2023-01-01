package org.usf.assertapi.core;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

class SequentialFutureTest {

	@Test
	void testGet() {
		assertEquals(0, assertDoesNotThrow(()-> new SequentialFuture<>(()-> 0).get()));
		assertEquals("", assertDoesNotThrow(()-> new SequentialFuture<>(()-> "").get()));
		var exp = new IOException();
		var act = assertThrows(ExecutionException.class, ()-> new SequentialFuture<>(()-> {throw exp;}).get());
		assertEquals(exp, act.getCause());
	}

	@Test
	void testCancel() {
		assertTrue(assertDoesNotThrow(()-> new SequentialFuture<>(null).cancel(true)));
		assertTrue(assertDoesNotThrow(()-> new SequentialFuture<>(null).cancel(false)));
	}

	@Test
	void testIsCancelled() {
		assertThrows(UnsupportedOperationException.class, ()-> new SequentialFuture<>(null).isCancelled());
	}

	@Test
	void testIsDone() {
		assertThrows(UnsupportedOperationException.class, ()-> new SequentialFuture<>(null).isDone());
	}

	@Test
	void testGet_timeout() {
		assertThrows(UnsupportedOperationException.class, ()-> new SequentialFuture<>(null).get(100l, SECONDS));
	}

}
