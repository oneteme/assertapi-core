package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static org.usf.assertapi.core.TestStatus.FAIL;
import static org.usf.assertapi.core.TestStatus.KO;
import static org.usf.assertapi.core.TestStatus.OK;
import static org.usf.assertapi.core.TestStatus.SKIP;
import static org.usf.assertapi.core.TestStep.CONTENT_TYPE;
import static org.usf.assertapi.core.TestStep.HTTP_CODE;
import static org.usf.assertapi.core.TestStep.RESPONSE_CONTENT;

import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.Getter;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResponseProxyComparator implements ResponseComparator {
	
	private final ResponseComparator comparator;
	private final Consumer<ApiAssertionsResult> tracer;
	private final ApiExecution expExecution;
	private final ApiExecution actExecution;
	private final ApiRequest request;

	public ResponseProxyComparator(ResponseComparator comparator, Consumer<ApiAssertionsResult> tracer, ServerConfig exServerConfig, ServerConfig acServerConfig, ApiRequest request) {
		this.comparator = comparator;
		this.tracer = tracer;
		this.expExecution = new ApiExecution(exServerConfig.buildRootUrl());
		this.actExecution = new ApiExecution(acServerConfig.buildRootUrl());
		this.request = request;
	}
	
	@Override
	public void assumeEnabled(boolean enable) {
		try {
			comparator.assumeEnabled(enable);
		}
		catch(Throwable e) {
			trace(SKIP, null);
			throw e;
		}
	}
	
	@Override
	public <T> T execute(boolean expexted, Supplier<T> c) {
		var o = expexted ? expExecution : actExecution;
		o.setStart(currentTimeMillis());
		try {
			return c.get();
		} finally {
			o.setEnd(currentTimeMillis());
 		}
	}

	@Override
	public void assertStatusCode(int expectedStatusCode, int actualStatusCode) {
		try {
			comparator.assertStatusCode(expectedStatusCode, actualStatusCode);
		}
		catch(Throwable e) {
			trace(KO, HTTP_CODE);
			throw e;
		}
	}

	@Override
	public void assertContentType(MediaType expectedContentType, MediaType actualContentType) {
		try {
			comparator.assertContentType(expectedContentType, actualContentType);
		}
		catch(Throwable e) {
			trace(KO, CONTENT_TYPE);
			throw e;
		}
	}

	@Override
	public void assertByteContent(byte[] expectedContent, byte[] actualContent) {
		try {
			comparator.assertByteContent(expectedContent, actualContent);
		}
		catch(Throwable e) {
			trace(KO, RESPONSE_CONTENT);
			throw e;
		}
	}

	@Override
	public void assertTextContent(String expectedContent, String actualContent) {
		try {
			comparator.assertTextContent(expectedContent, actualContent);
		}
		catch(Throwable e) {
			trace(KO, RESPONSE_CONTENT);
			throw e;
		}
	}
	
	@Override
	public void assertJsonContent(String expectedContent, String actualContent, boolean strict) {
		try {
			comparator.assertJsonContent(expectedContent, actualContent, strict);
		}
		catch(Throwable e) {
			trace(KO, RESPONSE_CONTENT);
			throw e;
		}
	}

	@Override
	public void assertJsonCompareResut(JSONCompareResult res) {
		//should not be call
	}
	
	@Override
	public void assertionFail(Throwable t) {
		trace(FAIL, null);
		comparator.assertionFail(t);
	}
	
	@Override
	public void assertOK() { 
		try {
			comparator.assertOK();
			trace(OK, null);
		}
		catch(Exception e) {
			trace(KO, null);
		}
	}
	
	private void trace(TestStatus status, TestStep step) {
		try {
			tracer.accept(new ApiAssertionsResult(
					request.getId(),
					expExecution,
					actExecution,
					status,
					step));
		}
		catch(Exception e) {
			log.warn("cannot trace this test : {}", e.getMessage());
		}
	}
}
