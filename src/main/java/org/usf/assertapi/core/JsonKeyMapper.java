package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_KEY_MAPPER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import com.jayway.jsonpath.DocumentContext;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class JsonKeyMapper extends ResponseTransformer<DocumentContext> {

	private final String xpath; //to object
	private final Map<String, String> map;
	
	public JsonKeyMapper(ReleaseTarget[] targets, String xpath, Map<String, String> map) {
		super(targets);
		this.xpath = requireNonEmpty(xpath, getType(), "xpath");
		this.map = requireNonEmpty(map, getType(), "Map<oldKey,newKey>");
	}
	
	@Override
	public void transform(DocumentContext json) {
		map.entrySet().forEach(e-> json.renameKey(xpath, e.getKey(), e.getValue())); //require string value
    }
	
	@Override
	public String getType() {
		return JSON_KEY_MAPPER.name();
	}
}
