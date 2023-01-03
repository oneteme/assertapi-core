package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import com.jayway.jsonpath.DocumentContext;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class JsonValueMapper extends ResponseTransformer<DocumentContext> {
	
	private final String xpath; //to string field
	private final Map<String, Object> map; 

	public JsonValueMapper(ReleaseTarget[] targets, String xpath, Map<String, Object> map) {
		super(targets);
		this.xpath = requireNonEmpty(xpath, ()-> getType() + " : require xpath");
		this.map   = requireNonEmpty(map,   ()-> getType() + " : require Map<oldValue,newValue>");
	}

	@Override
	public void transform(DocumentContext json) {//String::valueOf => Number & boolean equals with String key
		json.map(xpath, (o, c)-> map.containsKey(String.valueOf(o)) ? map.get(o.toString()) : o);
	}
	
	@Override
	public String getType() {
		return JSON_VALUE_MAPPER.name();
	}
}
