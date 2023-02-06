package org.usf.assertapi.core;

@FunctionalInterface
public interface ModelTransformer<T> extends PolymorphicType {

	T transform(T model);

}