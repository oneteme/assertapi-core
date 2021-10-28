package fr.enedis.teme.assertapi.core;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import org.springframework.web.client.RestTemplate;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiAssertionsFactory {

	private ResponseComparator comparator;
	private ServerConfig exServerConfig;
	private ServerConfig acServerConfig;
	private Consumer<ApiAssertionsResult> resultTracer;

	public ApiAssertionsFactory using(ResponseComparator comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public ApiAssertionsFactory comparing(ServerConfig expectedServerConf, ServerConfig actualServerConf) {
		this.exServerConfig = expectedServerConf;
		this.acServerConfig = actualServerConf;
		return this;
	}
	
	public ApiAssertionsFactory trace(Consumer<ApiAssertionsResult> trTemp) {
		this.resultTracer = trTemp;
		return this;
	}
	
	public ApiAssertionsFactory traceOn(String url) {
		var template = new RestTemplate();
		this.resultTracer = tr-> template.put(url, tr);
		return this;
	}
	
	public ApiAssertions build() {
		
		requireNonNull(comparator);
		return new DefaultApiAssertions(
				RestTemplateBuilder.build(requireNonNull(exServerConfig)),
				RestTemplateBuilder.build(requireNonNull(acServerConfig)),
				resultTracer == null ? comparator : new ResponseProxyComparator(comparator, resultTracer, exServerConfig, acServerConfig)); 
	}
	
}
