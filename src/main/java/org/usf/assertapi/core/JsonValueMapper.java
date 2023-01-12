package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jayway.jsonpath.DocumentContext;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class JsonValueMapper extends ResponseTransformer<DocumentContext> {
	
	private final String xpath;
	private final String regex;
	private final Map<String, Object> map;

	public JsonValueMapper(ReleaseTarget[] targets, String xpath, String regex, Map<String, Object> map) {
		super(targets);
		this.xpath = requireNonEmpty(xpath, getType(), "xpath");
		this.regex = regex;
		this.map   = requireNonEmpty(map, getType(), "Map<oldValue,newValue>");
	}

	@Override
	public void transform(DocumentContext json) {
		json.map(xpath, (o, c)-> transformValue(o));
	}
	
	@Override
	public String getType() {
		return JSON_VALUE_MAPPER.name();
	}

	private Object transformValue(Object value) {
		var strValue = String.valueOf(value);
		if(map.containsKey(String.valueOf(strValue))) {//String::valueOf => Number & boolean equals with String key
			return map.get(strValue);
		}
		if (regex != null && strValue.matches(regex)) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strValue);
			if (matcher.find()) { //TODO while !
				if (matcher.groupCount() == 0) {
					return groupValue(0, strValue);
				}
				var sb = new StringBuilder()
						.append(strValue.substring(0, matcher.start(1)))
						.append(groupValue(1, matcher.group(1)));
				for (int i=2; i<=matcher.groupCount(); i++) {
					sb.append(strValue.substring(matcher.end(i-1), matcher.start(i)))
					.append(groupValue(i, matcher.group(i)));
				}
				return sb.append(strValue.substring(matcher.end(matcher.groupCount()))).toString();
			}
		}
		return value;
	}
	
	private String groupValue(int index, String value) {
		var key = "$"+index;
		return String.valueOf(map.getOrDefault(key, "")).replace(key, value);
	}
}
