/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.util.regex.Pattern;

/** Identify PV names
 *  @author Kay Kasemir
 */
public class PVNameFilter
{
    final private static Pattern number = Pattern.compile("^-?[0-9]+.*");
        
    /** @param pvname Possible PV name as received from a link
     *  @return <code>true</code> if it looks like a valid PV name
     */
    public static boolean isPvName(final String pvname)
    {
        // Skip Hardware links
        if (pvname.startsWith("#"))
            return false;
        if (pvname.startsWith("@"))
            return false;
        // Skip contants
        if (number.matcher(pvname).matches())
            return false;
        return true;
    }
}
