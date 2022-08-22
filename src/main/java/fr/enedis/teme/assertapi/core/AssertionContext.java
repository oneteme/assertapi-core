package fr.enedis.teme.assertapi.core;

import static java.lang.System.getProperty;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssertionContext {
	
	public static final String CTX = "$ctx";
	public static final String CTX_ID = "$ctx-id";

	private final String user;
	private final String os;
	private final String address;

	public static AssertionContext buildContext() {
		
		return new AssertionContext(
				getProperty("user.name"), 
				getProperty("os.name"),
				getHostAddress());
	}
	
	public String toHeader() {
		try {
			return getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(this));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static AssertionContext parseHeader(ObjectMapper mapper, String header) {
		
		try {
			return mapper.readValue(getDecoder().decode(header), AssertionContext.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "?";
		}
	}
	
}
