package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseTransformer.TransformerType.*;
import static org.usf.assertapi.core.TypeComparatorConfig.ResponseType.JSON;

import java.util.HashSet;
import java.util.Set;

import static org.usf.assertapi.core.TypeComparatorConfig.ResponseType.CSV;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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
	private static final Set<Class<? extends RuntimeException>> errors = new HashSet<>(); 
	
	static {
		defaultMapper = Jackson2ObjectMapperBuilder.json().build().registerModule(new ParameterNamesModule());
		//register default ResponseComparisonConfig impl.
		registerSubCompareConfig(JsonComparatorConfig.class, JSON.name());
		registerSubCompareConfig(CsvComparatorConfig.class, CSV.name());
		//register default ResponseTransformer impl.
		registerSubTransformer(JsonXpathTransformer.class, XPATH_TRANSFORMER.name());
		registerSubTransformer(JsonXpathKeyTransformer.class, XPATH_KEY_TRANSFORMER.name());
		registerSubTransformer(JsonXpathValueTransformer.class, XPATH_VALUE_TRANSFORMER.name());
	}
	
	public static void registerSubCompareConfig(Class<? extends TypeComparatorConfig<?>> c, String name) {
		defaultMapper.registerSubtypes(new NamedType(c, name));
	}

	public static void registerSubTransformer(Class<? extends ResponseTransformer<?>> c, String name) {
		defaultMapper.registerSubtypes(new NamedType(c, name));
	}
	
	public static void registerAssertionFail(Class<? extends RuntimeException> c) {
		errors.add(c);
	}

	//TODO register custom auth. 
	public static ObjectMapper defaultMapper() {
		return defaultMapper;
	}
	
	public static boolean isAssertionFail(Throwable t) {
		return errors.stream().anyMatch(c-> c.isInstance(t));
	}
	
}
