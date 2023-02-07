package org.usf.assertapi.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeName;

class AbstractModelTransformerTest {

	@Test
	void testAbstractModelTransformer() {
		assertArrayEquals(new ReleaseTarget[] {STABLE}, newModelTransformer().getApplyOn()); //STABLE by default
		assertArrayEquals(new ReleaseTarget[] {LATEST}, newModelTransformer(LATEST).getApplyOn());
		assertArrayEquals(new ReleaseTarget[] {STABLE, LATEST}, newModelTransformer(STABLE, LATEST).getApplyOn());
	}

	@Test
	void testMatchTarget() {
		var mt = newModelTransformer();
		assertTrue(mt.matchTarget(STABLE));
		assertFalse(mt.matchTarget(LATEST));
		mt = newModelTransformer(STABLE, LATEST);
		assertTrue(mt.matchTarget(STABLE));
		assertTrue(mt.matchTarget(LATEST));
	}
	
	@Test
	void testToString() {
		assertEquals("null(STABLE)", newModelTransformer().toString()); //no annotation
		@JsonTypeName("dummy")
		class DummyClass extends AbstractModelTransformer<Object> {
			protected DummyClass() {super(new ReleaseTarget[] {STABLE, LATEST});}
			public Object transform(Object model) {return model;}
		}
		assertEquals("dummy(STABLE,LATEST)", new DummyClass().toString());
	}

	private static AbstractModelTransformer<Object> newModelTransformer(ReleaseTarget... applyOn) {
		return new AbstractModelTransformer<Object>(applyOn) {
			public Object transform(Object model) {return model;};
		};
	}
}
