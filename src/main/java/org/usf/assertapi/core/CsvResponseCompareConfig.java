package org.usf.assertapi.core;

import static org.usf.assertapi.core.ResponseType.CSV;

/**
 * 
 * @author u$f
 * @since
 *
 */
public final class CsvResponseCompareConfig implements ResponseCompareConfig {

	@Override
	public ResponseType getType() {
		return CSV;
	}

}
