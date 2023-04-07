package org.usf.assertapi.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonTypeName("BINARY")
public class BinaryDataComparator implements ModelComparator<byte[]>{

    @Override
    public CompareResult compare(byte[] expected, byte[] actual) {
        return new CompareResult(
        		Arrays.toString(expected), 
        		Arrays.toString(actual), 
        		Arrays.equals(expected, actual));
    }
}
