package org.usf.assertapi.core;

import static com.jayway.jsonpath.JsonPath.compile;
import static org.usf.assertapi.core.PolymorphicType.jsonTypeName;
import static org.usf.assertapi.core.Utils.flow;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeName("JSON_DATA_MAPPER")
public class JsonDataMapper extends AbstractModelTransformer<DocumentContext>  {

	private final JsonPath path;
	private final DataTransformer[] transformers;

	public JsonDataMapper(ReleaseTarget[] applyOn, String path, DataTransformer[] transformers) {
		super(applyOn);
		this.path = compile(requireNonEmpty(path, jsonTypeName(this.getClass()), "path"));
		this.transformers = requireNonEmpty(transformers, jsonTypeName(this.getClass()), "transformers");
	}
	
	@Override
	public DocumentContext transform(DocumentContext json) {
		return json.map(path, (o, c)-> flow(o, DataTransformer::transform, transformers));
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + path;
	}
}
