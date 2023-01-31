package org.usf.assertapi.core;

import static org.usf.assertapi.core.DataComparator.ResponseType.CSV;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@RequiredArgsConstructor //keep one default constructor
public final class CsvDataComparator implements DataComparator<String> {
	
	private final DataTransformer<String, String>[] transformers;

	@Override
	public CompareResult compare(String expected, String actual){
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	@Override
	public String getType() {
		return CSV.name();
	}
}
