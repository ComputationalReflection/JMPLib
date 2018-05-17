package jmplib.primitives.impl.util;

import java.util.regex.Pattern;

/**
 * Contains certain utility functions to help coding visitors.
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
public class VisitorUtil {
	/**
	 * Obtains the original class name from a version class name
	 * 
	 * @param className
	 * @return
	 */
	public static String parseVersionClassName(String className) {
		return className.split("_")[0].trim();
	}

	/**
	 * Obtains the class name only from a full package class specification
	 * 
	 * @param className
	 * @return
	 */
	public static String getClassName(String className) {
		String nameOnly;
		if (className.contains(".")) {
			String[] parts = className.split(Pattern.quote("."));
			nameOnly = parts[parts.length - 1].trim();
		} else
			nameOnly = className.trim();

		return nameOnly;
	}
}
