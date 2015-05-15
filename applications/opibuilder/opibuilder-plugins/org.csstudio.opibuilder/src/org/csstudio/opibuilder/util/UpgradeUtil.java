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
@SuppressWarnings("nls")
public final class UpgradeUtil {

    /**
     *The first version that supports PVManager.
     */
    public static final Version VERSION_WITH_PVMANAGER = new Version(3,2,6);

    private final static String doublePattern = "\\s*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*";
    private final static String doubleArrayPattern = doublePattern + "(," + doublePattern + ")+";

    /**Convert utility PV name to PVMangaer PV name.
     * @param pvName
     * @param oldVersion
     * @return the converted name.
     */
    public static String convertUtilityPVNameToPM(String pvName){

        // convert loc://pvName(fred) to loc://pvName("fred")
        if (pvName.startsWith("loc://")) {
                final int value_start = getFirstIndexHelper(pvName, 0);
                    if (value_start > 0) {
                        if(!pvName.matches(".+[^$]\\(.*\\)"))
                            return pvName;
                        final int value_end = pvName.lastIndexOf(')');
                        if (value_end > 0) {
                            String value_text = pvName.substring(value_start + 1,
                                    value_end);
                            if (!value_text.matches("\".*\"") &&
                                    !value_text.matches(doubleArrayPattern)) { // if it is not number array
                                try {
                                    Double.parseDouble(value_text);
                                } catch (Exception e) {
                                    return pvName.substring(0, value_start + 1)
                                            + "\"" + pvName.substring(value_start + 1, value_end) +
                                            "\"" + pvName.substring(value_end);
                                }
                            }
                        }
                    }
                return pvName;
        }


        if (pvName.startsWith("const://"))
        {
            // Old format example:
            // const://$(DID)_name_$(DID)(123, 456, $(M), 789)")

            // Find last bracket
            final int value_end = pvName.lastIndexOf(')');
            if (value_end <= 0)
                return pvName;

            // Find matching opening brace
            int value_start = value_end;
            int brackets = 1;
            while (--value_start > 0)
            {
                if (pvName.charAt(value_start) == ')')
                    ++brackets;
                else if (pvName.charAt(value_start) == '(')
                {
                    if (--brackets == 0)
                        break;
                }
            }
            if (brackets != 0)
                return pvName;

            final String value_text = pvName.substring(value_start + 1, value_end);

            // Return  const://myArray(12,34,56) as sim://const(12,23,56)
            if(value_text.matches(doubleArrayPattern))
                return "sim://const(" + value_text + ")";

            // Return  const://text("Text")   as   ="Text"
            if (value_text.matches("\".+\""))
                return "=" + value_text;

            // Return  const://parsable_numer(1.23e-4)   as   =1.23e-4
            try
            {
                Double.parseDouble(value_text);
                return "=" + value_text;
            }
            catch (Exception e)
            {
                // Ignore
            }

            // Not obvious number, nor quoted string.
            // Because of Bug discussion https://github.com/ControlSystemStudio/cs-studio/issues/412,
            // assume value that is one macro "$(Macro)" contains a number
            if (value_text.trim().startsWith("$("))
                return "="+value_text;

            // No idea what is inside value, so quote it
            return "=\"" + value_text + "\"";
        }

        if(pvName.startsWith("\"") && pvName.endsWith("\""))
            return "="+pvName;

        if(pvName.matches(doublePattern)){
            return "="+pvName;
        }

        return pvName;
    }


    private static int getFirstIndexHelper(String s, int from){
        if(s== null || s.isEmpty()) return -1;
        int i = s.indexOf('(', from);
        if(i<=0) return i;
        if(s.charAt(i-1) == '$'){
            int newStart=s.indexOf(')', i);
            if(newStart>=0)
                return getFirstIndexHelper(s, newStart);
        }
        return i;
    }

}


