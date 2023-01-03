package org.usf.assertapi.core.junit;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.usf.assertapi.core.Module;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonObjectMapper implements ArgumentConverter {
	
	public static ObjectMapper mapper = Module.defaultMapper();
	
	@Override
	public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
		try {
			return mapper.readValue((File)source, context.getParameter().getType());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}