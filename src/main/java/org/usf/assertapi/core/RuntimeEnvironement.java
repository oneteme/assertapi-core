package org.usf.assertapi.core;

import static java.lang.System.getProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@ToString	
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuntimeEnvironement {

	private static String prefix = "$api-";

	@With
	private final String user;
	private final String os;
	private final String jre;
	private final String address;
	private final String branch;

	public void push(BiConsumer<String, String> cons) {
		cons.accept(prefix+"user", user);
		cons.accept(prefix+"os", os);
		cons.accept(prefix+"jre", jre);
		cons.accept(prefix+"address", address);
		cons.accept(prefix+"branch", branch);
	}
	
	public static RuntimeEnvironement from(UnaryOperator<String> fn) {
		return new RuntimeEnvironement(
				fn.apply(prefix+"user"), 
				fn.apply(prefix+"os"), 
				fn.apply(prefix+"jre"), 
				fn.apply(prefix+"address"), 
				fn.apply(prefix+"branch"));
	}
	
	public static RuntimeEnvironement build() {
		return new RuntimeEnvironement(
				getProperty("user.name"), 
				getProperty("os.name"), 
				getProperty("java.version"),
				getHostAddress(),
				getLocalBranch());
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
			Process process = Runtime.getRuntime().exec(new String[]{ "cmd", "/C", "git rev-parse --abbrev-ref HEAD" }); //unix shell ?
		    try(InputStreamReader isr = new InputStreamReader(process.getInputStream());
		    	BufferedReader reader = new BufferedReader(isr)) {
		    	return reader.readLine();
		    }
		} catch (IOException e) {
			return null;
		}
	}
}
