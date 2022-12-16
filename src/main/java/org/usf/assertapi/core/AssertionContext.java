package org.usf.assertapi.core;

import static java.lang.System.getProperty;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

@ToString
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssertionContext {
	
	public static final String CTX = "$ctx";
	public static final String CTX_ID = "$ctx-id";

	@With
	private final String user;
	private final String os;
	private final String javaVersion;
	private final String address;
	private final String branch;

	public static AssertionContext buildContext() {
		return new AssertionContext(
				getProperty("user.name"), 
				getProperty("os.name"), 
				getProperty("java.version"),
				getHostAddress(),
				getLocalBranch());
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
			return null;
		}
	}
	
	static String getLocalBranch() {
		try {
			Process process = Runtime.getRuntime().exec(new String[]{ "cmd", "/C", "git rev-parse --abbrev-ref HEAD" });
		    try(InputStreamReader isr = new InputStreamReader(process.getInputStream());
		    	BufferedReader reader = new BufferedReader(isr)) {
		    	return reader.readLine();
		    }
		} catch (IOException e) {
			return null;
		}
	}
}
