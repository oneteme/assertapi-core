package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.TypeComparatorConfig.ResponseType.JSON;

import org.json.JSONException;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class JsonComparatorConfig implements TypeComparatorConfig<String> {

	private final boolean strict;
	private final ResponseTransformer<String>[] transformers;

	public JsonComparatorConfig(Boolean strict, ResponseTransformer<String>[] transformers) {
		this.strict = ofNullable(strict).orElse(true);
		this.transformers = transformers;
	}
	
	@Override
	public CompareResult compare(String expected, String actual) throws JSONException {
		try {
			if(transformers != null) {
				for(var t : transformers) {
					expected = t.transform(expected, STABLE);
					actual = t.transform(actual, LATEST);
				}
			}
			assertEquals(expected, actual, strict);
			return new CompareResult(expected, actual, true);
		} catch (AssertionError e) {
			return new CompareResult(expected, actual, false);
		}
	}

	@Override
	public String getType() {
		return JSON.name();
	}
}
