package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_FILTER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.stream.Stream;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class JsonPathFilter extends ResponseTransformer<DocumentContext, DocumentContext> {

	private final Stream<JsonPath> paths; //exclude | include ?
	
	public JsonPathFilter(ReleaseTarget[] targets, String[] paths) {
		super(targets);
		this.paths = Stream.of(requireNonEmpty(paths, getType(), "xpaths"))
				.map(JsonPath::compile);
	}
	
	@Override
	protected DocumentContext transform(DocumentContext json) {
		paths.forEach(json::delete);
		return json;
    }
	
	@Override
	public String getType() {
		return JSON_PATH_FILTER.name();
	}
}
