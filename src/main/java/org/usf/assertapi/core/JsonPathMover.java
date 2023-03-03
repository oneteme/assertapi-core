package org.usf.assertapi.core;

import static com.jayway.jsonpath.JsonPath.compile;
import static java.util.Objects.requireNonNullElse;
import static org.usf.assertapi.core.JsonDataComparator.jsonParser;
import static org.usf.assertapi.core.JsonPathMover.Action.PUT;
import static org.usf.assertapi.core.JsonPathMover.Action.SET;
import static org.usf.assertapi.core.PolymorphicType.jsonTypeName;
import static org.usf.assertapi.core.Utils.isJsonArray;
import static org.usf.assertapi.core.Utils.isJsonObject;
import static org.usf.assertapi.core.Utils.requireNonEmpty;
import static org.usf.assertapi.core.Utils.unsupportedOperation;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@JsonTypeName("JSON_PATH_MOVER")
public class JsonPathMover extends AbstractModelTransformer<DocumentContext> {

	private final JsonPath fromPath;
	private final JsonPath toPath;
	private final Action action;
	private final String key;
	
	public JsonPathMover(ReleaseTarget[] applyOn, String from, String to, Action action, String key) {
		super(applyOn);
		this.fromPath = compile(from);
		this.toPath = compile(to);
		this.action = requireNonNullElse(action, SET);
		this.key = action == PUT ? requireNonEmpty(key, jsonTypeName(this.getClass()), "key") : null; //else unused key
	}

	@Override
	public DocumentContext transform(DocumentContext json) {
		switch (action) {
		case SET  : return setOrigin(json);
		case ADD  : return addOrigin(json);
		case PUT  : return putOrigin(json);
		case MERGE: return mergeOrigin(json);
		default: throw unsupportedOperation("action", action.name());
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
		if(isJsonArray(json.read(toPath))) {
			json.add(toPath, json.read(fromPath));
			return json.delete(fromPath);
		}
		throw new IllegalArgumentException(toPath.getPath() + " : is not array");
	}

	private DocumentContext putOrigin(DocumentContext json) {
		if(isJsonObject(json.read(toPath))) {
			json.put(toPath, key, json.read(fromPath));
			return json.delete(fromPath);
		}
		throw new IllegalArgumentException(toPath.getPath()  + " : is not object");
	}

	private DocumentContext mergeOrigin(DocumentContext json) {
		var origin = json.read(fromPath);
		var target = json.read(toPath);
		if(isJsonArray(target)) {
			if(isJsonArray(origin)) {
				((JSONArray)origin).forEach(o-> json.add(toPath, o)); //filter items ==> Array[path]
			}
			else if(isJsonObject(origin)) {
				throw new UnsupportedOperationException("cannot merge object " + fromPath.getPath() + " with array " + toPath.getPath());
			}
			else {
				throw expectArray(fromPath);
			}
		}
		else if(isJsonObject(target))  {
			if(isJsonObject(origin)) {
				((Map<String, ?>)origin).entrySet()
				.forEach(e-> json.put(toPath, e.getKey(), e.getValue()));//filter fields ==> Map<path,path>
			}
			else if(isJsonArray(origin)) {
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
	
	public enum Action {
		SET, PUT, ADD, MERGE;
	}
	
}
