package fr.enedis.teme.assertapi.core;

import static java.util.Objects.requireNonNull;

import org.springframework.web.client.RestTemplate;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiAssertionsFactory {

	private RestTemplate exTemp;
	private RestTemplate acTemp;
	private RestTemplate trTemp;
	private ResponseComparator comparator;

	public ApiAssertionsFactory trace(RestTemplate trTemp) {
		this.trTemp = trTemp;
		return this;
	}

	public ApiAssertionsFactory compare(RestTemplate expectedTemp, RestTemplate actualTemp) {
		this.exTemp = expectedTemp;
		this.acTemp = actualTemp;
		return this;
	}
	
	public ApiAssertionsFactory compare(ServerConfig expectedServerConf, ServerConfig actualServerConf) {
		this.exTemp = RestTemplateBuilder.build(expectedServerConf);
		this.acTemp = RestTemplateBuilder.build(actualServerConf);
		return this;
	}
	
	public ApiAssertionsFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertions build() {
		
		requireNonNull(comparator);
		return new DefaultApiAssertions(
				requireNonNull(exTemp),
				requireNonNull(acTemp),
				trTemp == null ? comparator : new ResponseProxyComparator(comparator, trTemp)); 
	}

}
