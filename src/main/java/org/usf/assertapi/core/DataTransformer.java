package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;
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
public abstract class DataTransformer<T,R> implements PolymorphicType {

	private final ReleaseTarget[] applyOn;
	
	protected DataTransformer(ReleaseTarget[] applyOn) {
		this.applyOn = requireNonNullElse(applyOn, new ReleaseTarget[] {STABLE});
	}
	
	public boolean matchTarget(ReleaseTarget target) {
		return Stream.of(applyOn).anyMatch(target::equals);
	}

	protected abstract R transform(T resp); //response can be byte[] | string
	
	enum TransformerType {
		
		JSON_PATH_FILTER, JSON_PATH_MOVER, JSON_KEY_MAPPER, JSON_VALUE_MAPPER; 
	}
}
