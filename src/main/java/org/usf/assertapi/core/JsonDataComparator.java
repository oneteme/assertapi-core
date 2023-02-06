package org.usf.assertapi.core;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;
import static java.util.Objects.requireNonNullElse;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.Utils.flow;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.util.stream.Stream;

import org.json.JSONException;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.ParseContext;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@JsonTypeName("JSON")
public final class JsonDataComparator implements ModelComparator<String> {
	
	static final ParseContext jsonParser = using(defaultConfiguration().addOptions(SUPPRESS_EXCEPTIONS));

	private final boolean strict;
	private final AbstractModelTransformer<DocumentContext>[] transformers;

	public JsonDataComparator(Boolean strict, AbstractModelTransformer<DocumentContext>[] transformers) {
		this.strict = requireNonNullElse(strict, true);
		this.transformers = transformers;
	}
	
	@Override
	public CompareResult compare(String expected, String actual) {
		if(!isEmpty(transformers)) {
			expected = transform(expected, STABLE);
			actual = transform(actual, LATEST);
		}
		try {
			assertEquals(expected, actual, strict);
			return new CompareResult(expected, actual, true);
		} catch (AssertionError e) {
			return new CompareResult(expected, actual, false);
		} catch (JSONException e) {
			throw new ApiAssertionRuntimeException("error while parsing JSON content", e);
		}
	}

	@SuppressWarnings("unchecked")
	private final String transform(String resp, ReleaseTarget target){
		if(resp != null) {
			var arr = Stream.of(transformers)
					.filter(t-> t.matchTarget(target))
					.toArray(AbstractModelTransformer[]::new);
			if(arr.length > 0) {
				return flow(jsonParser.parse(resp), AbstractModelTransformer::transform, (AbstractModelTransformer<DocumentContext>[]) arr).jsonString();
			}
		}
		return resp;
	}
}
