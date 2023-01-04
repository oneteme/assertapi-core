package org.usf.assertapi.core.junit;

import static org.usf.assertapi.core.Utils.defaultMapper;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonObjectMapper implements ArgumentConverter {
	
	public static ObjectMapper mapper = defaultMapper(); //TODO inject ?
	
	@Override
	public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
		try {
			return mapper.readValue((File)source, context.getParameter().getType());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}