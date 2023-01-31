package org.usf.assertapi.core;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static com.jayway.jsonpath.JsonPath.using;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;
import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.toList;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.usf.assertapi.core.DataComparator.ResponseType.JSON;
import static org.usf.assertapi.core.ReleaseTarget.LATEST;
import static org.usf.assertapi.core.ReleaseTarget.STABLE;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.util.stream.Stream;

import org.json.JSONException;

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
public final class JsonDataComparator implements DataComparator<String> {
	
	static final ParseContext jsonParser = using(defaultConfiguration().addOptions(SUPPRESS_EXCEPTIONS));

	private final boolean strict;
	private final DataTransformer<DocumentContext, DocumentContext>[] transformers;

	public JsonDataComparator(Boolean strict, DataTransformer<DocumentContext, DocumentContext>[] transformers) {
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
	
	private final String transform(String resp, ReleaseTarget target){
		if(resp != null) {
			var list = Stream.of(transformers)
					.filter(t-> t.matchTarget(target))
					.collect(toList());
			if(!list.isEmpty()) {
				var doc = jsonParser.parse(resp);
				for(var t : list) {
					doc = t.transform(doc);
				}
				resp = doc.jsonString();
			}
		}
		return resp;
	}

	@Override
	public String getType() {
		return JSON.name();
	}
}
