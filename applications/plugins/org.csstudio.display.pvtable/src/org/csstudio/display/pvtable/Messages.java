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

    public static String ConfigDlg_Description;

    public static String ConfigDlg_Title;

    public static String ConfigDlg_Tolerance;

    public static String ConfigDlg_Tolerance_TT;

    public static String ConfigDlg_ToleranceError;

    public static String ConfigDlg_UpdatePeriod;

    public static String ConfigDlg_UpdatePeriod_TT;

    public static String ConfigDlg_UpdatePeriodError;

    public static String Editor_SaveTask;

    public static String EmptyRowMarker;

    public static String PVTable;

    public static String StartStop_Start;

    public static String StartStop_Start_TT;

    public static String StartStop_Stop;

    public static String StartStop_Stop_TT;

    public static String TableCol_Name;

    public static String TableCol_ReadbackPV;

    public static String TableCol_ReadbackValue;

    public static String TableCol_SavedReadbackValue;

    public static String TableCol_SavedValue;

    public static String TableCol_Sel;

    public static String TableCol_Time;

    public static String TableCol_Value;
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
