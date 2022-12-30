package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@type")
@Getter
public abstract class ResponseTransformer<T> {

	private final ReleaseTarget target;
	
	ResponseTransformer(ReleaseTarget target) {
		this.target = ofNullable(target).orElse(STABLE);
	}
	
	public final T transform(T resp, ReleaseTarget rt) {
		return resp == null || getTarget() != rt ? resp: transform(resp) ;
	}

	abstract String getType();
	
	abstract T transform(T resp); //response can be byte[] | string
	
	enum TransformerType {
		
		XPATH_TRANSFORMER, XPATH_KEY_TRANSFORMER, XPATH_VALUE_TRANSFORMER; 
	}
}
