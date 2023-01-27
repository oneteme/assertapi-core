package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;

import java.util.stream.Stream;

import com.jayway.jsonpath.DocumentContext;
import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public abstract class ResponseTransformer<T> implements PolymorphicType {

	private final ReleaseTarget[] targets;
	
	protected ResponseTransformer(ReleaseTarget[] targets) {
		this.targets = requireNonNullElse(targets, new ReleaseTarget[] {STABLE});
	}
	
	public boolean matchTarget(ReleaseTarget target) {
		return Stream.of(targets).anyMatch(target::equals);
	}

	protected abstract void transform(T resp); //response can be byte[] | string

	enum TransformerType {
		
		JSON_PATH_FILTER, JSON_PATH_MOVER, JSON_KEY_MAPPER, JSON_VALUE_MAPPER; 
	}
}
