package org.usf.assertapi.core;

import java.net.URI;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

public class RootUriTemplateHandler implements UriTemplateHandler {

	private final String rootUri;

	private final UriTemplateHandler handler;

	protected RootUriTemplateHandler(UriTemplateHandler handler) {
		Assert.notNull(handler, "Handler must not be null");
		this.rootUri = null;
		this.handler = handler;
	}

	/**
	 * Create a new {@link RootUriTemplateHandler} instance.
	 * @param rootUri the root URI to be used to prefix relative URLs
	 */
	public RootUriTemplateHandler(String rootUri) {
		this(rootUri, new DefaultUriBuilderFactory());
	}

	/**
	 * Create a new {@link RootUriTemplateHandler} instance.
	 * @param rootUri the root URI to be used to prefix relative URLs
	 * @param handler the delegate handler
	 */
	public RootUriTemplateHandler(String rootUri, UriTemplateHandler handler) {
		Assert.notNull(rootUri, "RootUri must not be null");
		Assert.notNull(handler, "Handler must not be null");
		this.rootUri = rootUri;
		this.handler = handler;
	}

	@Override
	public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
		return this.handler.expand(apply(uriTemplate), uriVariables);
	}

	@Override
	public URI expand(String uriTemplate, Object... uriVariables) {
		return this.handler.expand(apply(uriTemplate), uriVariables);
	}

	private String apply(String uriTemplate) {
		if (StringUtils.startsWithIgnoreCase(uriTemplate, "/")) {
			return getRootUri() + uriTemplate;
		}
		return uriTemplate;
	}

	public String getRootUri() {
		return this.rootUri;
	}

	/**
	 * Add a {@link RootUriTemplateHandler} instance to the given {@link RestTemplate}.
	 * @param restTemplate the {@link RestTemplate} to add the handler to
	 * @param rootUri the root URI
	 * @return the added {@link RootUriTemplateHandler}.
	 */
	public static RootUriTemplateHandler addTo(RestTemplate restTemplate, String rootUri) {
		Assert.notNull(restTemplate, "RestTemplate must not be null");
		var handler = new RootUriTemplateHandler(rootUri, restTemplate.getUriTemplateHandler());
		restTemplate.setUriTemplateHandler(handler);
		return handler;
	}

}