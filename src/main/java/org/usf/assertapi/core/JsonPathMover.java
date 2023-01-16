package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_MOVER;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;

public class JsonPathMover extends ResponseTransformer<DocumentContext> {

	private final String originXpath;
	private final String targetXpath;
	private final Map<String, String> map;
	

	public JsonPathMover(ReleaseTarget[] targets, String originXpath, String targetXpath, Map<String, String> map) {
		super(targets);
		this.originXpath = originXpath;
		this.targetXpath = targetXpath;
		this.map = map;
	}
	
	@Override
	void transform(DocumentContext json) {
		var o = json.read(originXpath);
		if(o instanceof Map) {
			@SuppressWarnings("unchecked")
			var copy = new LinkedHashMap<>((Map<String, Object>) o); 
			if(isEmpty(map)) {
				for(var e : copy.entrySet()){
					json.put(targetXpath, e.getKey(), e.getValue());
					json.delete(originXpath + "." + e.getKey());
				}
//				json.delete(originXpath);
			}
			else {
				for(var e : map.entrySet()){
					json.put(targetXpath, e.getValue(), copy.get(e.getKey()));
					json.delete(originXpath + "." + e.getKey());
				}
			}
		}
		else {
			//if list ? 
			throw new ApiAssertionRuntimeException(originXpath + " is not object");
		}
	}
	
	@Override
	public String getType() {
		return JSON_PATH_MOVER.name();
	}
	
}
