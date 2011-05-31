/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.sysmon;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.utility.sysmon.messages"; //$NON-NLS-1$

    public static String PreferencePage_HistSize;
    public static String PreferencePage_Restart;
    public static String PreferencePage_ScanDelay;
    public static String PreferencePage_Title;
    public static String PreferencePage_ValidHistSize;
    public static String PreferencePage_ValidScanDelay;

    public static String SysMon_FreeLabel;
    public static String SysMon_Free_TT;
    public static String SysMon_GCLabel;
    public static String SysMon_GC_TT;
    public static String SysMon_Max_TT;
    public static String SysMon_MaxLabel;
    public static String SysMon_MemFormat;
    public static String SysMon_SpanLabel;
    public static String SysMon_Span_TT;
    public static String SysMon_TimeSpanFormat;
    public static String SysMon_Total_TT;
    public static String SysMon_TotalLabel;
    
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // Prevent instantiation
    }
}
