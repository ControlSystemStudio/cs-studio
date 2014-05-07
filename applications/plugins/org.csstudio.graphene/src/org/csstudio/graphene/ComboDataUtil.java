package org.csstudio.graphene;

import java.util.List;

public class ComboDataUtil {
	public static String[] toStringArray(List<?> values) {
		String[] result = new String[values.size()];
		for (int i = 0; i < values.size(); i++) {
			result[i] = values.get(i).toString();
		}
		return result;
	}
}
