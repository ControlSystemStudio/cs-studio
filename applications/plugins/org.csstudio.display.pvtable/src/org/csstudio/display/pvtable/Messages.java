/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.display.pvtable.messages"; //$NON-NLS-1$


    public static String Alarm;
    public static String Delete;
    public static String Delete_TT;
    public static String DeSelectAll;
    public static String DeSelectAll_TT;
    public static String EnterPositiveTolerance;
    public static String EnterTolerance;
    public static String Error;
    public static String PV;
    public static String Restore;
    public static String Restore_TT;
    public static String Saved;
    public static String SelectAll;
    public static String SelectAll_TT;
    public static String Snapshot;
    public static String Snapshot_TT;
    public static String Time;
    public static String Tolerance;
    public static String Tolerance_TT;
    public static String Value;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // prevent instantiation
    }
}
