package org.usf.assertapi.core;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_XML;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
interface ClientResponseWrapper {
	
	ExecutionInfo getRequestExecution();
	
	int getStatusCodeValue();

	MediaType getContentType();
	
	Map<String, List<String>> getHeaders();
	
	String getResponseBodyAsString();
	
	String getResponseBodyAsString(Charset charset);
	
	byte[] getResponseBodyAsByteArray();
	
	default String getContentTypeValue() {
		return ofNullable(getContentType()).map(MediaType::getType).orElse(null);
	}
	
	default boolean isTextCompatible(){
		return getContentType() != null && Stream.of(
				APPLICATION_JSON, APPLICATION_XML,
				TEXT_PLAIN, TEXT_HTML, TEXT_XML)
				.anyMatch(getContentType()::isCompatibleWith);
	}
	
	default boolean isJsonCompatible(){
		return getContentType() != null 
				&& APPLICATION_JSON.isCompatibleWith(getContentType());
	}	

	@Getter
	@RequiredArgsConstructor
	public final class ResponseEntityWrapper implements ClientResponseWrapper {
		
		private final ResponseEntity<byte[]> entity;
		private final ExecutionInfo requestExecution;
		
		@Override
		public int getStatusCodeValue() {
			return entity.getStatusCodeValue();
		}
		
		@Override
		public MediaType getContentType() {
			return ofNullable(entity.getHeaders()).map(HttpHeaders::getContentType).orElse(null);
		}
		
		@Override
		public Map<String, List<String>> getHeaders() {
			return entity.getHeaders();
		}
		
		@Override
		public String getResponseBodyAsString() {
			return ofNullable(entity.getBody()).map(String::new).orElse(null);
		}

		@Override
		public String getResponseBodyAsString(Charset charset) {
			return ofNullable(entity.getBody()).map(t-> new String(t, charset)).orElse(null);
		}
		
		@Override
		public byte[] getResponseBodyAsByteArray() {
			return entity.getBody();
		}
	}

	@Getter
	@RequiredArgsConstructor
	@SuppressWarnings("serial")
	public final class RestClientResponseExceptionWrapper extends Exception implements ClientResponseWrapper {
		
		private final transient RestClientResponseException exception;
		private final transient ExecutionInfo requestExecution;
		
		@Override
		public int getStatusCodeValue() {
			return exception.getRawStatusCode();
		}
		
		@Override
		public MediaType getContentType() {
			return ofNullable(exception.getResponseHeaders()).map(HttpHeaders::getContentType).orElse(null);
		}
		
		@Override
		public Map<String, List<String>> getHeaders() {
			return exception.getResponseHeaders();
		}
		
		@Override
		public String getResponseBodyAsString() {
			return exception.getResponseBodyAsString();
		}

		@Override
		public String getResponseBodyAsString(Charset charset) {
			return exception.getResponseBodyAsString(charset);
		}
		
		@Override
		public byte[] getResponseBodyAsByteArray() {
			return exception.getResponseBodyAsByteArray();
		}
	}

	@Getter
	@RequiredArgsConstructor
	public final class HttpRequestWrapper implements ClientResponseWrapper {
		
		private final HttpRequest request;
		private final ExecutionInfo requestExecution;

		@Override
		public ExecutionInfo getRequestExecution() {
			return requestExecution;
		}

		@Override
		public int getStatusCodeValue() {
			return request.requireUniqueStatus();
		}

		@Override
		public MediaType getContentType() {
			return ofNullable(request.getHeader(CONTENT_TYPE))
					.map(MediaType::parseMediaType)
					.orElse(null);
		}

		@Override
		public Map<String, List<String>> getHeaders() {
			return request.getHeaders();
		}

		@Override
		public String getResponseBodyAsString() {
			return request.bodyAsString();
		}

		@Override
		public String getResponseBodyAsString(Charset charset) {
			return new String(request.getBody(), charset);
		}

		@Override
		public byte[] getResponseBodyAsByteArray() {
			return request.getBody();
		}
	}
}