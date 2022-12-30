package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.XPATH_VALUE_TRANSFORMER;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public class JsonXpathValueTransformer extends ResponseTransformer<String> {

	public JsonXpathValueTransformer(ReleaseTarget target) {
		super(target);
	}

	@Override
	public String getType() {
		return XPATH_VALUE_TRANSFORMER.name();
	}

	@Override
	public String transform(String resp) {
		return null;
	}

}
