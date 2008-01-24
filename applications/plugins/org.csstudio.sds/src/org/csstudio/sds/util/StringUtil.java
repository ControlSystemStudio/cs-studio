package org.csstudio.sds.util;

import java.util.List;

public class StringUtil {
	public static String convertListToSingleString(List list) {
		return convertListToSingleString(list, "\n");
	}

	public static String convertListToSingleString(List list, String lineEnd) {
		StringBuffer sb = new StringBuffer();

		for (Object o : list) {
			sb.append(o.toString());
			sb.append(lineEnd);
		}

		return sb.toString();
	}
}
