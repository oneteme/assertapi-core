package org.usf.assertapi.core;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author u$f
 * @since 1.0
 *
 */
@Getter
@JsonTypeName("CSV")
@RequiredArgsConstructor //keep one default constructor
public final class CsvDataComparator implements ModelComparator<String> {
	
	private final AbstractModelTransformer<String>[] transformers;

	@Override
	public CompareResult compare(String expected, String actual){
		throw new UnsupportedOperationException("not yet implemented");
	}
}
