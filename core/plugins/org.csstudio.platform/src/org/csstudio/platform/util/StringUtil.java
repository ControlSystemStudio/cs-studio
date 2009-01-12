package org.csstudio.platform.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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

	public static String capitalize(String s) {
		String result = s;
		if (hasLength(s)) {
			result = s.substring(0,1).toUpperCase() + s.substring(1);
		}
		return result;
	}

	public static boolean hasLength(String s) {
		return (s != null && !"".equals(s));
	}

	public static String toSeparatedString(Collection<String> collection, String separator) {
		StringBuffer sb = new StringBuffer();
		
		if(!collection.isEmpty()) {
			Iterator<String> it = collection.iterator();
			sb.append(it.next());
			
			while(it.hasNext()) {
				sb.append(separator);
				sb.append(it.next());
			}
		}
		
		return sb.toString();
	}

	public static String trimNull(String s) {
		return hasLength(s)?s:"";
	}
}
