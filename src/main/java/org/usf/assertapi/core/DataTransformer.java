package org.usf.assertapi.core;

/**
 * 
 * <p>Standard data transform, must not depends on ModelTransfomer</p>
 * 
 * @author u$f
 * @since 1.0
 * 
 */
@FunctionalInterface
public interface DataTransformer extends PolymorphicType {
	
	abstract Object transform(Object value);
}
