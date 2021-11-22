package fr.enedis.teme.assertapi.core;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class HttpQuery extends HttpRequest {

	private boolean debug = false;
	private boolean enable = true;
	private boolean strict = true;
	private boolean parallel = true;
	private String description;
	private HttpRequest expected;
	private HttpRequest actual;

	@Override
	public HttpQuery build() {
		if(getUri() == null) {
			requireNonNull(expected).build();
			requireNonNull(actual).build();
		}
		else {
			expected = actual = super.build().copy(); //copy avoid recursive serialization exception
		}
		return this;
	}

	@Override
	public String toString() {
		return description == null || description.isEmpty() 
				? actual.toString() 
				: description + " : " + actual.toString();
	}
	
}