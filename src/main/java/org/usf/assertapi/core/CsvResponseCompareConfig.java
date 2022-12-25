package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseType.CSV;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
public final class CsvResponseCompareConfig implements ResponseCompareConfig {

	@Override
	public ResponseType getType() {
		return CSV;
	}

}
