package org.usf.assertapi.core;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.usf.assertapi.core.ClientAuthenticator.ServerAuthMethod.BEARER;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class BearerClientAuthenticator implements ClientAuthenticator {

	@Override
	public void authorization(HttpHeaders headers, ServerAuth auth) {
		headers.setBearerAuth(requireNonNullElseGet(auth.getToken(), ()-> fetchIdToken(auth)));
	}
	
	@Deprecated(forRemoval = true) //specific nova
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
	
	@Override
	public String getType() {
		return BEARER.name();
	}
}
