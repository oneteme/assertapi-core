package org.usf.assertapi.core;

import static java.util.stream.Collectors.joining;
import static org.usf.assertapi.core.PolymorphicType.typeName;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;

import java.util.stream.Stream;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public abstract class AbstractModelTransformer<T> implements ModelTransformer<T> {

	private final ReleaseTarget[] applyOn;
	
	protected AbstractModelTransformer(ReleaseTarget[] applyOn) {
		this.applyOn = Utils.isEmpty(applyOn) ? new ReleaseTarget[] {STABLE} : applyOn;
	}
	
	public boolean matchTarget(ReleaseTarget target) {
		return Stream.of(applyOn).anyMatch(target::equals);
	}
	
	@Override
	public String toString() {
		return typeName(this.getClass()) + Stream.of(applyOn).map(Object::toString).collect(joining(",", "(", ")"));
	}
}
