package org.usf.assertapi.core;

import static com.jayway.jsonpath.JsonPath.compile;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.Arrays;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public abstract class JsonAbstractValueMapper extends ResponseTransformer<DocumentContext, DocumentContext>  {

	private final JsonPath path; //to object

	protected JsonAbstractValueMapper(ReleaseTarget[] targets, String path) {
		super(targets);
		this.path = compile(requireNonEmpty(path, getType(), "xpath"));
	}
	
	@Override
	protected DocumentContext transform(DocumentContext json) {
		json.map(path, (o, c)-> transformValue(o));
		return json;
	}
	
	protected abstract Object transformValue(Object value);
	
	@Override
	public String toString() {
		return Arrays.toString(getTargets()) + " : " + path;
	}

}
