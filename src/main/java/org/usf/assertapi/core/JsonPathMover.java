package org.usf.assertapi.core;

import static com.jayway.jsonpath.JsonPath.compile;
import static java.util.Objects.requireNonNullElse;
import static org.usf.assertapi.core.JsonContentComparator.jsonParser;
import static org.usf.assertapi.core.JsonPathMover.Action.SET;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_MOVER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

public class JsonPathMover extends ResponseTransformer<DocumentContext, DocumentContext> {

	private final JsonPath originPath;
	private final JsonPath targetPath;
	private final Action action;
	private final String key;
	
	public JsonPathMover(ReleaseTarget[] targets, String origin, String target, Action action, String key) {
		super(targets);
		this.originPath = compile(origin);
		this.targetPath = compile(target);
		this.action = requireNonNullElse(action, SET);
		this.key = key;
	}
	
	@Override
	protected DocumentContext transform(DocumentContext json) {
		switch (action) {
		case SET  : return setOrigin(json);
		case ADD  : return addOrigin(json);
		case PUT  : return putOrigin(json);
		case MERGE: return mergeOrigin(json);
		default: throw new UnsupportedOperationException("unsupported action " + action);
		}
	}
	
	private DocumentContext setOrigin(DocumentContext json) { //warn field / map
		var origin = json.read(originPath);
		if(targetPath.getPath().equals("$")) {//root
			return jsonParser.parse(origin);
		}
		else {
			json.set(targetPath, origin);
			return json.delete(originPath);
		}
	}
	
	private DocumentContext addOrigin(DocumentContext json) {
		if(isArray(json.read(targetPath))) {
			json.add(targetPath, json.read(originPath));
			return json.delete(originPath);
		}
		throw new IllegalArgumentException(targetPath.getPath() + " : is not array");
	}

	private DocumentContext putOrigin(DocumentContext json) {
		if(isObject(json.read(targetPath))) {
			//require field
			json.put(targetPath, requireNonEmpty(key, getType(), "field"), json.read(originPath));
			return json.delete(originPath);
		}
		throw new IllegalArgumentException(targetPath.getPath()  + " : is not object");
	}

	private DocumentContext mergeOrigin(DocumentContext json) {
		var origin = json.read(originPath);
		var target = json.read(targetPath);
		if(isArray(target)) {
			if(isArray(origin)) {
				((JSONArray)origin).forEach(o-> json.add(targetPath, o)); //filter items
			}
			else if(isObject(origin)) {
				throw new UnsupportedOperationException("cannot merge object " + originPath.getPath() + " with array " + targetPath.getPath());
			}
			else {
				throw expectArray(originPath);
			}
		}
		else if(isObject(target))  {
			if(isObject(origin)) {
				((Map<String, ?>)origin).entrySet()
				.forEach(e-> json.put(targetPath, e.getKey(), e.getValue()));//filter fields
			}
			else if(isArray(origin)) {
				throw new UnsupportedOperationException("cannot merge array " + originPath.getPath() + " with object " + targetPath.getPath());
			}
			else {
				throw expectObject(targetPath);
			}
		}
		else {
			throw expectArrayOrObject(targetPath);
		}
		return json.delete(originPath);
	}
	
	private static IllegalAccessError expectObject(JsonPath xpath) {
		return new IllegalAccessError(xpath.getPath() + " is not an object");
	}

	private static IllegalAccessError expectArray(JsonPath xpath) {
		return new IllegalAccessError(xpath.getPath() + " is not an array");
	}
	
	private IllegalAccessError expectArrayOrObject(JsonPath xpath) {
		return new IllegalAccessError(xpath.getPath() + " must be an object or array");
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
	
	public enum Action {
		SET, PUT, ADD, MERGE;
	}
	
}
