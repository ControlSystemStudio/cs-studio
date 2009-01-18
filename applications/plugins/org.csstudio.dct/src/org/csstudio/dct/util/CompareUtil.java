package org.csstudio.dct.util;

import org.csstudio.dct.model.IElement;

/**
 * Collection of utility methods that help comparing things.
 * 
 * @author Sven Wende
 * 
 */
public final class CompareUtil {
	private CompareUtil() {
	}
	/**
	 * Compares two Objects.
	 * 
	 * @param o1
	 *            Object 1
	 * @param o2
	 *            Object 2
	 * @return true, if both Object equal
	 */
	public static boolean equals(Object o1, Object o2) {
		boolean result = false;

		if (o1 == null) {
			if (o2 == null) {
				result = true;
			}
		} else {
			if (o1.equals(o2)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Compares the id´s of two elements.
	 * 
	 * @param o1
	 *            element 1
	 * @param o2
	 *            element 2
	 * @return true, if the id´s of both elements equal or both elements are
	 *         null
	 */
	public static boolean idsEqual(IElement o1, IElement o2) {
		boolean result = false;

		if (o1 != null) {
			if (o2 != null) {
				result = equals(o1.getId(), o2.getId());
			}
		} else {
			if (o2 == null) {
				result = true;
			}
		}

		return result;
	}
}
