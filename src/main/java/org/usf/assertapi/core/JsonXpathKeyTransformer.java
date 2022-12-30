package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.XPATH_KEY_TRANSFORMER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import com.jayway.jsonpath.JsonPath;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class JsonXpathKeyTransformer extends ResponseTransformer<String> {

	private final String xpath;
	private final Map<String, String> map;
	
	public JsonXpathKeyTransformer(ReleaseTarget target, String xpath, Map<String, String> map) {
		super(target);
		this.xpath = requireNonEmpty(xpath, ()-> XPATH_KEY_TRANSFORMER + " : require xpath");
		this.map = requireNonEmpty(map, ()->  XPATH_KEY_TRANSFORMER + " : require Map<oldKey,newKey>");
	}
	
	@Override
	public String getType() {
		return XPATH_KEY_TRANSFORMER.name();
	}
	
	@Override
	public String transform(String resp) {
		var json = JsonPath.parse(resp);
		map.entrySet().forEach(e-> json.renameKey(xpath, e.getKey(), e.getValue()));
		return json.jsonString();
    }
    
    
}
