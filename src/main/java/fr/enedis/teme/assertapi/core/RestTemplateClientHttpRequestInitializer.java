package fr.enedis.teme.assertapi.core;

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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class RestTemplateClientHttpRequestInitializer implements ClientHttpRequestInitializer {
	
	private static final String BASIC_AUTH = "Basic ";
	private static final String BEARER_AUTH = "Bearer ";
	
	private final String authorizationValue;

	@Override
	public void initialize(ClientHttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		if (this.authorizationValue != null && !headers.containsKey(AUTHORIZATION)) {
			headers.set(AUTHORIZATION, authorizationValue);
		}
	}
	
	static RestTemplateClientHttpRequestInitializer init(ServerConfig conf) {
		String authorizationValue = null;
    	if(conf.getAuthMethod() != null) {
	    	switch (requireNonNull(conf.getAuthMethod())) {
	    	case "basic":
	    		authorizationValue = BASIC_AUTH + encodeBasicAuth(requireNonNull(conf.getUsername()), requireNonNull(conf.getPassword()), null);
	    		break;
	    	case "token":
	    		authorizationValue = BEARER_AUTH + requireNonNull(conf.getToken());
	    		break;
	    	case "novaToken" : 
	    		authorizationValue = BASIC_AUTH + encodeBasicAuth("", fetchIdToken(conf), null);
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown method " + conf.getAuthMethod());
	    	}
    	}
    	return new RestTemplateClientHttpRequestInitializer(authorizationValue);
	}

    private static String fetchIdToken(ServerConfig conf) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(requireNonNull(conf.getUsername()), requireNonNull(conf.getPassword()));
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("scope", "openid");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map<String, String>> resp = new RestTemplate().exchange(requireNonNull(conf.getAccessTokenUrl()), POST, request, 
        		new ParameterizedTypeReference<Map<String,String>>() {});
        return resp.getBody().get("id_token");
    }

}
