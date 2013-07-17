/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.osgi.framework.Version;


/**
* Utility Class help to process the differences between version upgrade.
* @author Xihui Chen
*/
public final class UpgradeUtil {
	
	/**
	 *The first version that supports PVManager. 
	 */
	public static final Version VERSION_WITH_PVMANAGER = new Version(3,2,6);
	
	private final static String doublePattern = "\\s*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*"; //$NON-NLS-1$
	private final static String doubleArrayPattern = doublePattern + "(," + doublePattern + ")+"; //$NON-NLS-1$ //$NON-NLS-2$

	/**Convert utility PV name to PVMangaer PV name.
	 * @param pvName
	 * @param oldVersion
	 * @return the converted name.
	 */	
	public static String convertUtilityPVNameToPM(String pvName){		
		
		// convert loc://pvName(fred) to loc://pvName("fred")
		if (pvName.startsWith("loc://")) { //$NON-NLS-1$
				final int value_start = getFirstIndexHelper(pvName, 0); //$NON-NLS-1$
					if (value_start > 0) {
						if(!pvName.matches(".+[^$]\\(.*\\)")) //$NON-NLS-1$
							return pvName;						
						final int value_end = pvName.lastIndexOf(')'); //$NON-NLS-1$
						if (value_end > 0) {
							String value_text = pvName.substring(value_start + 1,
									value_end);
							if (!value_text.matches("\".*\"") && //$NON-NLS-1$ 
									!value_text.matches(doubleArrayPattern)) { // if it is not number array
								try {
									Double.parseDouble(value_text);
								} catch (Exception e) {
									return pvName.substring(0, value_start + 1)
											+ "\"" + pvName.substring(value_start + 1, value_end) + //$NON-NLS-1$
											"\"" + pvName.substring(value_end); //$NON-NLS-1$
								}
							}
						}
					}
				return pvName;
		}
		
		
		if(pvName.startsWith("const://")) {//$NON-NLS-1$			
			final int value_start = pvName.indexOf('('); //$NON-NLS-1$
			if (value_start > 0) {
				final int value_end = pvName.lastIndexOf(')'); //$NON-NLS-1$
				if (value_end > 0) {
					String value_text = pvName.substring(value_start + 1,
							value_end);
					//convert const://myArray(12,34,56) to sim://const(12,23,56)
					if(value_text.matches(doubleArrayPattern))
						return "sim://const(" + value_text + ")";	//$NON-NLS-1$ //$NON-NLS-2$					
					
					//const://myString(fred) to ="fred"
					if (!value_text.matches("\".+\"")){  //$NON-NLS-1$ 							
						try {
							Double.parseDouble(value_text);
						} catch (Exception e) {
							return "=\""+value_text+"\""; //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					//const://mvPV(123) to =123
					return "="+value_text; //$NON-NLS-1$
				}
			}			
		}
		
		if(pvName.startsWith("\"") && pvName.endsWith("\""))//$NON-NLS-1$ //$NON-NLS-2$
			return "="+pvName;  //$NON-NLS-1$		
		
		if(pvName.matches(doublePattern)){			
			return "="+pvName; //$NON-NLS-1$
		}	
		
		return pvName;
	}	

	
	private static int getFirstIndexHelper(String s, int from){
		if(s== null || s.isEmpty()) return -1;
		int i = s.indexOf('(', from); //$NON-NLS-1$
		if(i<=0) return i;
		if(s.charAt(i-1) == '$'){  //$NON-NLS-1$
			int newStart=s.indexOf(')', i); //$NON-NLS-1$
			if(newStart>=0)
				return getFirstIndexHelper(s, newStart);
		}
		return i;			
	}
	
} 


