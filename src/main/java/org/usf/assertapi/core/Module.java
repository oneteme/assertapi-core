package org.usf.assertapi.core;

import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BASIC;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BEARER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.XPATH_KEY_TRANSFORMER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.XPATH_TRANSFORMER;
import static org.usf.assertapi.core.ResponseTransformer.TransformerType.XPATH_VALUE_TRANSFORMER;
import static org.usf.assertapi.core.TypeComparatorConfig.ResponseType.CSV;
import static org.usf.assertapi.core.TypeComparatorConfig.ResponseType.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

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
	private static final Map<String, Class<? extends ClientAuthenticator>> clientAuthenticators = new HashMap<>();
	
	static {
		defaultMapper = json().build().registerModule(new ParameterNamesModule());
		//register TypeComparatorConfig implementations
		registerTypeComparatorConfig(JsonComparatorConfig.class, JSON.name());
		registerTypeComparatorConfig(CsvComparatorConfig.class, CSV.name());
		//register ResponseTransformer implementations
		registerResponseTransformer(JsonXpathTransformer.class, XPATH_TRANSFORMER.name());
		registerResponseTransformer(JsonXpathKeyTransformer.class, XPATH_KEY_TRANSFORMER.name());
		registerResponseTransformer(JsonXpathValueTransformer.class, XPATH_VALUE_TRANSFORMER.name());
		//register ClientAuthenticator implementations
		registerClientAuthenticator(BasicClientAuthenticator.class, BASIC.name());
		registerClientAuthenticator(BearerClientAuthenticator.class, BEARER.name());
	}
	
	public static void registerTypeComparatorConfig(Class<? extends TypeComparatorConfig<?>> c, String name) {
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
