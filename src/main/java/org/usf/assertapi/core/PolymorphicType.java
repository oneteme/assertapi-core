package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.NonNull;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "@type")
public interface PolymorphicType {

	static String jsonTypeName(@NonNull Class<? extends PolymorphicType> type) {
		return ofNullable(type.getAnnotation(JsonTypeName.class))
				.map(JsonTypeName::value)
				.orElse(null);
	}
	
	static String toSnakeCase(Class<?> clazz) {
		return compile("([A-Z])([a-z0-9]*)")
				.matcher(clazz.getSimpleName())
				.replaceAll(m-> m.start() == 0 ? "$1$2" : "_$1$2");
	}
}
