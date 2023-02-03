package org.usf.assertapi.core;

import static java.lang.String.valueOf;
import static org.usf.assertapi.core.PolymorphicType.typeName;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeName("DATA_MAPPER")
public final class DataMapper implements DataTransformer {
	
	private final Map<String, Object> map;
	
	public DataMapper(Map<String, Object> map) {
		this.map = requireNonEmpty(map, typeName(this.getClass()), "Map<oldValue|regex,newValue>");
		this.map.keySet().forEach(Pattern::compile); // verify regex
	}

	@Override
	public Object transform(Object value) {
		var strValue = valueOf(value); //String::valueOf => matching also with number & boolean
		return map.containsKey(strValue) 
				? map.get(strValue) 
				: map.entrySet().stream()
				.filter(e-> strValue.matches(e.getKey()))
				.findAny()
				.map(e-> replaceOrMap(strValue, e))
				.orElse(value);
	}
	
	static Object replaceOrMap(String value, Entry<String, Object> e) {
		return e.getValue() instanceof String 
				? value.replaceFirst(e.getKey(), (String)e.getValue()) 
				: e.getValue();
	}
}
