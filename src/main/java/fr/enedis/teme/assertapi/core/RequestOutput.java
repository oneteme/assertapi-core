package fr.enedis.teme.assertapi.core;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public final class RequestOutput {
	
	private String charset;
	private String[] excludePaths;
	
	public RequestOutput build() {
		charset = ofNullable(charset).map(c-> c.trim().toUpperCase().replace('_', '-')).orElse(UTF_8.name());
		return this;
	}
	
	public RequestOutput copy() {
		var out = new RequestOutput();
		out.setCharset(charset);
		out.setExcludePaths(excludePaths);
		return out;
	}
}
