package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseType.CSV;

public final class CsvResponseCompareConfig implements ResponseCompareConfig {

	@Override
	public ResponseType getType() {
		return CSV;
	}

}
