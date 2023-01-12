package org.usf.assertapi.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public final class StringBytesDeserializer extends JsonDeserializer<byte[]> {

	@Override
	public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return p.getCodec().readTree(p).toString().getBytes();
	}

}
