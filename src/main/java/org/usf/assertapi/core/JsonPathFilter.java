package org.usf.assertapi.core;

import static java.util.stream.Collectors.toList;
import static org.usf.assertapi.core.PolymorphicType.typeName;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeName("JSON_PATH_FILTER")
public final class JsonPathFilter extends AbstractModelTransformer<DocumentContext> {

	private final List<JsonPath> paths; //exclude | include ?
	
	public JsonPathFilter(ReleaseTarget[] applyOn, String[] paths) {
		super(applyOn);
		this.paths = Stream.of(requireNonEmpty(paths, typeName(this.getClass()), "paths"))
				.map(JsonPath::compile)
				.collect(toList());
	}
	
	@Override
	public DocumentContext transform(DocumentContext json) {
		paths.forEach(json::delete);
		return json;
    }
}
