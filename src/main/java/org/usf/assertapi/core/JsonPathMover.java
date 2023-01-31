package org.usf.assertapi.core;

import static com.jayway.jsonpath.JsonPath.compile;
import static java.util.Objects.requireNonNullElse;
import static org.usf.assertapi.core.DataTransformer.TransformerType.JSON_PATH_MOVER;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.assertapi.core.JsonPathMover.Action.PUT;
import static org.usf.assertapi.core.JsonPathMover.Action.SET;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

public class JsonPathMover extends DataTransformer<DocumentContext, DocumentContext> {

	private final JsonPath fromPath;
	private final JsonPath toPath;
	private final Action action;
	private final String key;
	
	public JsonPathMover(ReleaseTarget[] applyOn, String from, String to, Action action, String key) {
		super(applyOn);
		this.fromPath = compile(from);
		this.toPath = compile(to);
		this.action = requireNonNullElse(action, SET);
		this.key = action == PUT ? requireNonEmpty(key, getType(), "field") : key; //else unused key
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
	
	private DocumentContext setOrigin(DocumentContext json) { //warn key
		var origin = json.read(fromPath);
		if(toPath.getPath().equals("$")) {//root
			return jsonParser.parse(origin);
		}
		else {
			json.set(toPath, origin);
			return json.delete(fromPath);
		}
	}
	
	private DocumentContext addOrigin(DocumentContext json) {
		if(isArray(json.read(toPath))) {
			json.add(toPath, json.read(fromPath));
			return json.delete(fromPath);
		}
		throw new IllegalArgumentException(toPath.getPath() + " : is not array");
	}

	private DocumentContext putOrigin(DocumentContext json) {
		if(isObject(json.read(toPath))) {
			json.put(toPath, key, json.read(fromPath));
			return json.delete(fromPath);
		}
		throw new IllegalArgumentException(toPath.getPath()  + " : is not object");
	}

	private DocumentContext mergeOrigin(DocumentContext json) {
		var origin = json.read(fromPath);
		var target = json.read(toPath);
		if(isArray(target)) {
			if(isArray(origin)) {
				((JSONArray)origin).forEach(o-> json.add(toPath, o)); //filter items
			}
			else if(isObject(origin)) {
				throw new UnsupportedOperationException("cannot merge object " + fromPath.getPath() + " with array " + toPath.getPath());
			}
			else {
				throw expectArray(fromPath);
			}
		}
		else if(isObject(target))  {
			if(isObject(origin)) {
				((Map<String, ?>)origin).entrySet()
				.forEach(e-> json.put(toPath, e.getKey(), e.getValue()));//filter fields
			}
			else if(isArray(origin)) {
				throw new UnsupportedOperationException("cannot merge array " + fromPath.getPath() + " with object " + toPath.getPath());
			}
			else {
				throw expectObject(toPath);
			}
		}
		else {
			throw expectArrayOrObject(toPath);
		}
		return json.delete(fromPath);
	}
	
	private static IllegalAccessError expectObject(JsonPath path) {
		return new IllegalAccessError(path.getPath() + " is not an object");
	}

	private static IllegalAccessError expectArray(JsonPath path) {
		return new IllegalAccessError(path.getPath() + " is not an array");
	}
	
	private IllegalAccessError expectArrayOrObject(JsonPath path) {
		return new IllegalAccessError(path.getPath() + " must be an object or array");
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
