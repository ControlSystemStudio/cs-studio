package org.csstudio.dct.util;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;

/**
 * Collection of utility methods that help comparing things.
 * 
 * @author Sven Wende
 * 
 */
public class CompareUtil {
	/**
	 * Compares two Objects.
	 * @param s1 Object 1
	 * @param s2 Object 2
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
	
	public static boolean idsEqual(IElement o1, IElement o2) {
		boolean result = false;
		
		if(o1!=null) {
			if(o2!=null) {
				result = equals(o1.getId(), o2.getId());
			}
		} else {
			if(o2==null) {
				result = true;
			}
		}
		
		return result;
	}
}
