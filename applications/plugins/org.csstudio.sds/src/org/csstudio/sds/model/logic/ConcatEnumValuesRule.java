package org.csstudio.sds.model.logic;

import java.util.Arrays;

public class ConcatEnumValuesRule implements IRule {

	public Object evaluate(Object[] arguments) {
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < arguments.length; i++) {
			if(i>0) {
				result.append(" ");
			}
			Object potentialArray = arguments[i];

			if (potentialArray instanceof int[]) {
				result.append(Arrays.toString((int[]) potentialArray));
			} else if (potentialArray instanceof boolean[]) {
				result.append(Arrays.toString((boolean[]) potentialArray));
			} else if (potentialArray instanceof long[]) {
				result.append(Arrays.toString((long[]) potentialArray));
			} else if (potentialArray instanceof byte[]) {
				result.append(Arrays.toString((byte[]) potentialArray));
			} else if (potentialArray instanceof char[]) {
				result.append(Arrays.toString((char[]) potentialArray));
			} else if (potentialArray instanceof double[]) {
				result.append(Arrays.toString((double[]) potentialArray));
			} else if (potentialArray instanceof float[]) {
				result.append(Arrays.toString((float[]) potentialArray));
			} else if (potentialArray instanceof short[]) {
				result.append(Arrays.toString((short[]) potentialArray));
			} else if (potentialArray instanceof Object[]) {
				result.append(Arrays.toString((Object[]) potentialArray));
			} else {
				result.append(potentialArray.toString());
			}
		}
		return result.toString();

	}

}
