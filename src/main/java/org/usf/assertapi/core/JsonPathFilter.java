package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_FILTER;
import static org.usf.assertapi.core.Utils.requireNonEmpty;

import java.util.stream.Stream;

import com.jayway.jsonpath.DocumentContext;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class JsonPathFilter extends ResponseTransformer<DocumentContext> {

	private final String[] xpaths;
	private final boolean exclude;
	
	public JsonPathFilter(ReleaseTarget[] targets, String[] xpaths) {
		super(targets);
		this.xpaths = requireNonEmpty(xpaths, ()-> getType() + " : require xpath");
		this.exclude = true; //always true, TODO false not working
	}
	
	@Override
	public void transform(DocumentContext json) {
		Stream.of(xpaths).forEach(json::delete);
    }
	
	@Override
	public String getType() {
		return JSON_PATH_FILTER.name();
	}
}