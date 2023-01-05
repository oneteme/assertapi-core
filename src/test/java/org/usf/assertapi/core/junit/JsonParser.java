package org.usf.assertapi.core.junit;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.support.AnnotationConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonParser implements ArgumentConverter, AnnotationConsumer<ConvertWithJsonParser> {
	
	private ConvertWithJsonParser annotation;

	@Override
	public void accept(ConvertWithJsonParser annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
		var mapper = annotation == null 
				? defaultMapper()
				: definedMapper();
		try {
			return mapper.readValue((File)source, context.getParameter().getType());
		} catch (IOException e) {
			throw new ArgumentConversionException("error while reading file " + source, e);
		}
	}
	
	private ObjectMapper definedMapper() {
		try {
			return (ObjectMapper) annotation.clazz()
					.getDeclaredMethod(annotation.method())
					.invoke(null);
		} catch (Exception e) {
			throw new ArgumentConversionException("error while instantiating " + annotation.clazz(), e);
		}
	}
	
	/**
	 * do not rename this method
	 * 
	 * @see org.usf.assertapi.core.junit.ConvertWithJsonParser
	 * 
	 */
	static ObjectMapper defaultMapper() {
		return new ObjectMapper();
	}
}