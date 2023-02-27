package org.usf.assertapi.core;

import static java.lang.System.currentTimeMillis;
import static org.usf.assertapi.core.Utils.sizeOf;

import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.ClientResponseWrapper.HttpRequestWrapper;
import org.usf.assertapi.core.Utils.EmptyValueException;

public final class DisconnectedAssertionExecutor extends ConnectedAssertionExecutor {
	
	public DisconnectedAssertionExecutor(RestTemplate latestReleaseTemp) {
		super(null, latestReleaseTemp);
	}

	@Override
	ClientResponseWrapper runStable(ApiRequest api) {
		var res = api.response();
		if(res == null) {
			throw new EmptyValueException("ApiRequest", "staticResponse");
		}
		var ms = currentTimeMillis();
		var body = loadBody(res);
		if(body != res.getBody()) {
			res = (StaticResponse) res.setBody(body);
		}
		var exe = new ExecutionInfo(ms, currentTimeMillis(), res.getStatus(), sizeOf(res.getBody()));
		return new HttpRequestWrapper(res, exe);
	}

}
