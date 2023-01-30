package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class JsonDefaultValueMapper extends JsonAbstractValueMapper {
	
	private final Map<String, Object> map;
	
	public JsonDefaultValueMapper(ReleaseTarget[] targets, String path, Map<String, Object> map) {
		super(targets, path);
		this.map = requireNonEmpty(map, getType(), "Map<oldValue,newValue>");
	}

	protected Object transformValue(Object value) {
		var strValue = String.valueOf(value); //String::valueOf => Number & boolean equals with String key
		if(map.containsKey(strValue)) {
			return map.get(strValue);
		}
		var entry = map.entrySet().stream().filter(e-> strValue.matches(e.getKey())).findAny();
		if(entry.isPresent()) {
			var o = entry.get().getValue();
			return o instanceof String 
					? strValue.replaceFirst(entry.get().getKey(), (String)o) 
					: o;
		}
		return value;
	}

	@Override
	public String getType() {
		return JSON_VALUE_MAPPER.name();
	}
	
}
