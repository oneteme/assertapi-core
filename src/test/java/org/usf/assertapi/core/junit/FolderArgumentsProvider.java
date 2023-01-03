package org.usf.assertapi.core.junit;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.util.StringUtils.isEmpty;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

public final class FolderArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<FolderSource> {
	
	private FolderSource fs; //relative | absolute

	@Override
	public void accept(FolderSource ds) {
		this.fs = ds;
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		var method = context.getTestMethod().orElseThrow();
		var clazz = context.getTestClass().orElseThrow();
		var folders = new File(clazz.getResource(fs.path()).toURI()).listFiles(filter());
		if(method.getParameterCount() == 0) {
			return Stream.of(folders).map(f-> arguments()); //no args
		}
		if(method.getParameterCount() == 1) {
			if(ArgumentsAccessor.class.isAssignableFrom(method.getParameters()[0].getClass())){
				var fn = typeResolver(fs.defaultType());
				return Stream.of(folders).map(f-> arguments(Stream.of(f.listFiles()).map(fn).toArray()));
			}
		}
		return Stream.of(folders)
				.map(f-> arguments(Stream.of(method.getParameters()).map(p-> attachedResource(f, p)).toArray()));
	}
	
	private Object attachedResource(File folder, Parameter arg) {
		File[] res = fs.mode().matchingFiles(arg, folder);
		if(res.length == 0) {
			return null; //TODO primitive types ? 
		}
		if(res.length == 1) {
			var type = arg.getAnnotation(ConvertWith.class) == null ? arg.getType() : fs.defaultType();
			return typeResolver(type).apply(res[0]);
		}
		throw new IllegalArgumentException(arg.getName() + " : to many resources found");
	}
	
	private Function<File, Object> typeResolver(Class<?> c) {
		if(c.equals(File.class)) {
			return f-> f;
		}
		if(c.equals(URI.class)) {
			return f-> f.toURI();
		}
		if(c.equals(Path.class)) {
			return f-> f.toPath();
		}
		if(c.equals(InputStream.class)) {
			return f-> {
				try {
					return f.toURI().toURL().openStream();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};
		}
		if(c.equals(String.class)) {
			return f-> {
				try {
					return readString(f.toPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};
		}
		throw new UnsupportedOperationException("Unsupported type " + c );
	}
	
	private FileFilter filter(){
    	return isEmpty(fs.pattern()) ?
    		f-> f.isDirectory() : 
    		f-> f.isDirectory() && f.getName().matches(fs.pattern());
	}
}