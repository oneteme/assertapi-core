package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.XPATH_TRANSFORMER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.stream.Stream;

import com.jayway.jsonpath.JsonPath;

import lombok.Getter;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
public final class JsonXpathTransformer extends ResponseTransformer<String> {

	private final String[] xpaths;
	private final boolean exclude;
	
	public JsonXpathTransformer(ReleaseTarget target, String[] xpaths, Boolean exclude) {
		super(target);
		this.xpaths = requireNonEmpty(xpaths, ()-> XPATH_TRANSFORMER + " : require xpath");
		this.exclude = true; //always true, TODO false not working
	}
	
	@Override
	public String getType() {
		return XPATH_TRANSFORMER.name();
	}
	
	@Override
	public String transform(String resp) {
		var json = JsonPath.parse(resp);
		Stream.of(xpaths).forEach(json::delete);
		return json.jsonString();
    }
	
}
