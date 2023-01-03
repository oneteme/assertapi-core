package org.usf.assertapi.core.junit;

import static java.lang.Character.MIN_VALUE;
import static org.usf.assertapi.core.junit.FolderSource.MatchingMode.SMART;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

import org.junit.jupiter.params.provider.ArgumentsSource;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(FolderArgumentsProvider.class)
public @interface FolderSource {
	
	String path() default ""; //root

	String pattern() default ""; //regex
	
	MatchingMode mode() default SMART;
	
	Class<?> defaultType() default File.class; // FILE | URI | PATH | InputStream | String
	
	enum MatchingMode {
		
		STRICT {
			@Override
			public File[] matchingFiles(Parameter arg, File folder) {
				return folder.listFiles(f-> f.isFile() && f.getName().equalsIgnoreCase(arg.getName())); //filesys ignore case
			}
		},
		SMART {
			@Override
			public File[] matchingFiles(Parameter arg, File folder) {
				var argName = arg.getName().replace('_', MIN_VALUE).toLowerCase();
				return folder.listFiles(f-> f.isFile() && argName.contains(formatFilename(f.getName())));
			}
		};
		
		public abstract File[] matchingFiles(Parameter arg, File folder);

		private static String formatFilename(String filename) {
			var idx = filename.lastIndexOf('.');
			if(idx > -1) {
				filename = filename.substring(0, idx);
			}
			return filename.replaceAll("\\s|-|_|\\.", "").toLowerCase();
		}
	}
}
