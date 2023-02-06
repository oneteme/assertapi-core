package org.usf.assertapi.core;

import static com.jayway.jsonpath.JsonPath.compile;
import static org.usf.assertapi.core.PolymorphicType.jsonTypeName;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeName("JSON_KEY_MAPPER")
public final class JsonKeyMapper extends AbstractModelTransformer<DocumentContext> {

	private final JsonPath path; //to object
	private final Map<String, String> map;
	
	public JsonKeyMapper(ReleaseTarget[] applyOn, String path, Map<String, String> map) {
		super(applyOn);
		this.path = compile(requireNonEmpty(path, jsonTypeName(this.getClass()), "path"));
		this.map = requireNonEmpty(map, jsonTypeName(this.getClass()), "Map<oldKey,newKey>");
	}
	
	@Override
	public DocumentContext transform(DocumentContext json) {
		map.entrySet().forEach(e-> json.renameKey(path, e.getKey(), e.getValue())); //require string value
		return json;
    }
}
