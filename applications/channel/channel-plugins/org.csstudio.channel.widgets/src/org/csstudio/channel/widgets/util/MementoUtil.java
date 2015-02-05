package org.csstudio.channel.widgets.util;

import java.util.Arrays;
import java.util.List;

public class MementoUtil {
	public static String toCommaSeparated(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String property : list) {
			sb.append(property).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public static List<String> fromCommaSeparated(String string) {
		return Arrays.asList(string.split(","));
	}
}
