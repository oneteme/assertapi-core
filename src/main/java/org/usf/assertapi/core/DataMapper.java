package org.usf.assertapi.core;

import static java.lang.String.valueOf;
import static org.usf.assertapi.core.PolymorphicType.jsonTypeName;
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

	final Map<String, Object> map;
	
	public DataMapper(Map<String, Object> map, String avoidOnlyOneArg) {
		this.map = requireNonEmpty(map, jsonTypeName(this.getClass()), "map<oldValue|regex,newValue>");
		this.map.keySet().forEach(Pattern::compile); // verify regex
	}

	@Override
	public Object transform(Object value) {
		var strValue = valueOf(value); //String::valueOf => matching also with number & boolean
		if(map.containsKey(strValue)) {
			return map.get(strValue);
		}
		var res = map.entrySet().stream()
				.filter(e-> strValue.matches(e.getKey()))
				.findAny();
		return res.isEmpty() ? value : replaceOrMap(strValue, res.get());

	}
	
	static Object replaceOrMap(String value, Entry<String, Object> e) {
		return e.getValue() instanceof String 
				? value.replaceFirst(e.getKey(), (String)e.getValue()) 
				: e.getValue();
	}
}
