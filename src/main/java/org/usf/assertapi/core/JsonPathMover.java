package org.usf.assertapi.core;

import static java.util.Objects.requireNonNullElse;
import static org.usf.assertapi.core.JsonContentComparator.jsonParser;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_MOVER;
import static org.usf.assertapi.core.Utils.isEmpty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.jayway.jsonpath.DocumentContext;

import net.minidev.json.JSONArray;

public class JsonPathMover extends ResponseTransformer<DocumentContext> {

	private final String originXpath;
	private final String targetXpath;
	private final Map<String, String> map;
	private final boolean removeOrigin;
	
	public JsonPathMover(ReleaseTarget[] targets, String originXpath, String targetXpath, Map<String, String> map, Boolean removeOrigin) {
		super(targets);
		this.originXpath = originXpath;
		this.targetXpath = targetXpath;
		this.map = map;
		this.removeOrigin = requireNonNullElse(removeOrigin, true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	void transform(DocumentContext json) {
		var origin = json.read(originXpath);
		var target = json.read(targetXpath);
		if(isArray(target)) {
			if(isEmpty(map)) {
				json.add(targetXpath, origin);
			}
			else {
				throw new ApiAssertionRuntimeException("moving any => array : doesn't support map configuration");
			}
		}
		else if(isObject(target)) {
			if(isObject(origin)) {
				moveObject(json, (Map<String, Object>) origin);
			}
			else if(isArray(origin)) {
				moveArray(json, (JSONArray) origin);
			}
			else {
				throw new ApiAssertionRuntimeException(originXpath + " is not object");
			}
		}
		else {
			throw new ApiAssertionRuntimeException("targetXpath must be an array or object");
		}
		if(removeOrigin) {
			json.delete(originXpath);
		}
	}
	
	private void moveObject(DocumentContext json, Map<String, Object> o) {
		Map<String, ?> entries;
		Consumer<Entry<String, ?>> consumer;
		if(isEmpty(map)) {
			entries = new LinkedHashMap<>(o); //copy : read/write
			consumer = e-> json.put(targetXpath, e.getKey(), e.getValue());
		}
		else {
			entries = map;
			consumer = e-> json.put(targetXpath, e.getValue().toString(), o.get(e.getKey())); 
		}
		if(!removeOrigin) {
			consumer = consumer.andThen(e-> json.delete(originXpath + "." + e.getKey()));
		} //else remove parent
		entries.entrySet().forEach(consumer);
	}

	private void moveArray(DocumentContext json, JSONArray arr) {
		if(isEmpty(map)) {
			throw new ApiAssertionRuntimeException("moving array => object : require Map<xpath, newKey> configuration");
		}
		var doc = jsonParser.parse(arr);
		map.entrySet().forEach(e-> json.put(targetXpath, e.getValue(), doc.read(e.getKey())));
	}
	
	private static boolean isObject(Object o) {
		return o instanceof Map;
	}
	
	private static boolean isArray(Object o) {
		return o instanceof JSONArray;
	}
	
	@Override
	public String getType() {
		return JSON_PATH_MOVER.name();
	}
	
}
