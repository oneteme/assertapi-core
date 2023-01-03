package org.usf.assertapi.core;

import static com.jayway.jsonpath.Configuration.defaultConfiguration;
import static com.jayway.jsonpath.Option.SUPPRESS_EXCEPTIONS;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BASIC;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BEARER;
import static org.usf.assertapi.core.ContentComparator.ResponseType.CSV;
import static org.usf.assertapi.core.ContentComparator.ResponseType.JSON;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_KEY_MAPPER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_PATH_FILTER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.JSON_VALUE_MAPPER;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Module {

	private static final ObjectMapper defaultMapper;
	private static final ParseContext jsonParser;
	private static final Map<String, Class<? extends ClientAuthenticator>> clientAuthenticators = new HashMap<>();
	
	static {
		defaultMapper = json().build().registerModule(new ParameterNamesModule());
		jsonParser = JsonPath.using(defaultConfiguration().addOptions(SUPPRESS_EXCEPTIONS)); //renameKey skip err if not exists
		//register TypeComparatorConfig implementations
		registerTypeComparatorConfig(JsonContentComparator.class, JSON.name());
		registerTypeComparatorConfig(CsvContentComparator.class, CSV.name());
		//register ResponseTransformer implementations
		registerResponseTransformer(JsonPathFilter.class, JSON_PATH_FILTER.name());
		registerResponseTransformer(JsonKeyMapper.class, JSON_KEY_MAPPER.name());
		registerResponseTransformer(JsonValueMapper.class, JSON_VALUE_MAPPER.name());
		//register ClientAuthenticator implementations
		registerClientAuthenticator(BasicClientAuthenticator.class, BASIC.name());
		registerClientAuthenticator(BearerClientAuthenticator.class, BEARER.name());
	}
	
	public static void registerTypeComparatorConfig(Class<? extends ContentComparator<?>> c, String name) {
		defaultMapper.registerSubtypes(new NamedType(c, name));
	}

	public static void registerResponseTransformer(Class<? extends ResponseTransformer<?>> c, String name) {
		defaultMapper.registerSubtypes(new NamedType(c, name));
	}
	
	public static void registerClientAuthenticator(Class<? extends ClientAuthenticator> c, String name) {
		clientAuthenticators.put(name, c);
	}
	
	public static ObjectMapper defaultMapper() {
		return defaultMapper;
	}
	
	public static ParseContext defaultJsonParser() {
		return jsonParser;
	}
	
	public static ClientAuthenticator getClientAuthenticator(String name) {
		var auth = clientAuthenticators.get(name);
		if(auth == null) {
			throw new NoSuchElementException("no such class for " + name);
		}
		try {
			return auth.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("error while creating new instance of " + auth.getName(), e);
		}
	}
	
}
