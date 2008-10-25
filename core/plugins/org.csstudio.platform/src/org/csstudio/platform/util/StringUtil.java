package org.csstudio.platform.util;

import java.util.Arrays;

public class StringUtil {

	public static final String printArrays(Object value) {
		String result = null;

		if (value == null) {
			result = "null";
		} else if (value instanceof double[]) {
			result = Arrays.toString((double[]) value);
		} else if (value instanceof long[]) {
			result = Arrays.toString((long[]) value);
		} else if (value instanceof String[]) {
			result = Arrays.toString((String[]) value);
		} else if (value instanceof Object[]) {
			result = Arrays.toString((Object[]) value);
		} else {
			result = value.toString();
		}

		return result;
	}
}
