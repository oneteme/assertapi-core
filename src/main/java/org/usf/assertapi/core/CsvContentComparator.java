package org.usf.assertapi.core;

import static org.usf.assertapi.core.ContentComparator.ResponseType.CSV;

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
public final class CsvContentComparator implements ContentComparator<String> {
	
	private final ResponseTransformer<String>[] transformers;

	@Override
	public CompareResult compare(String expected, String actual){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getType() {
		return CSV.name();
	}

}
