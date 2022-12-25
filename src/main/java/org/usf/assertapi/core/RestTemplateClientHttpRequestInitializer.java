package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.encodeBasicAuth;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class RestTemplateClientHttpRequestInitializer implements ClientHttpRequestInitializer {
	
	private static final String BASIC_AUTH = "Basic ";
	private static final String BEARER_AUTH = "Bearer ";
	
	private final String authorizationValue;

	@Override
	public void initialize(ClientHttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		if (this.authorizationValue != null) {
			headers.addIfAbsent(AUTHORIZATION, authorizationValue); //injected in headers
		}
	}
	
	static RestTemplateClientHttpRequestInitializer init(ServerConfig conf) {
		String authorizationValue = null;
    	if(conf.getAuth() != null) {
    		var auth = conf.getAuth();
			var authMethod =  ServerAuthMethod.valueOf(requireNonNull(auth.getAuthMethod()).toUpperCase().replace("-", "_"));
	    	switch (authMethod) {
				case NO_AUTH:
					break;
				case BASIC:
					authorizationValue = BASIC_AUTH + encodeBasicAuth(requireNonNull(auth.getUsername()), requireNonNull(auth.getPassword()), null);
					break;
				case TOKEN:
					authorizationValue = BEARER_AUTH + requireNonNull(auth.getToken());
					break;
				case NOVA_BASIC:
					authorizationValue = BASIC_AUTH + encodeBasicAuth("", fetchIdToken(auth), null);
					break;
				case NOVA_TOKEN :
					authorizationValue = BASIC_AUTH + encodeBasicAuth("", requireNonNull(auth.getToken()), null);
					break;
	    		default:
	    			throw new IllegalArgumentException("Unknown method " + auth.getAuthMethod());
	    	}
    	}
    	return new RestTemplateClientHttpRequestInitializer(authorizationValue);
	}

    private static String fetchIdToken(ServerAuth auth) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(requireNonNull(auth.getUsername()), requireNonNull(auth.getPassword()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("scope", "openid");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map<String, String>> resp = new RestTemplate().exchange(requireNonNull(auth.getAccessTokenUrl()), POST, request, 
        		new ParameterizedTypeReference<Map<String,String>>() {});
        return resp.getBody().get("id_token");
    }

}
