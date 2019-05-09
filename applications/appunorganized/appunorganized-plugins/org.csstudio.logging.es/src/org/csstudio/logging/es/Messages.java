/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es;

import org.eclipse.osgi.util.NLS;

/**
 * Externalized Strings
 * 
 * @author Kay Kasemir
 * @author Eclipse String Externalization Wiard
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.logging.es.messages"; //$NON-NLS-1$

    public static String Error;
    public static String Export;
    public static String ExportErrorFmt;
    public static String ExportTitle;
    public static String Filter_and;
    public static String Filter_Message;
    public static String Filter_MinSeverity;
    public static String Filter_NoFilter;
    public static String Filter_not;
    public static String Filter_Property;
    public static String Filter_PropertyTT;
    public static String Filter_Value;
    public static String Filter_ValuePatternHelp;
    public static String Filter_ValueTT;
    public static String GUI_ErrorFilter;
    public static String GUI_ErrorInit;
    public static String GUI_ErrorTimes;
    public static String GUI_LabelEnd;
    public static String GUI_LabelFilter;
    public static String GUI_LabelStart;
    public static String GUI_LabelTimes;
    public static String GUI_ShowDetail;
    public static String GUI_ToolTipEnd;
    public static String GUI_ToolTipFilter;
    public static String GUI_ToolTipRefresh;
    public static String GUI_ToolTipStart;
    public static String GUI_ToolTopTimes;
    public static String MessageDetail;
    public static String MessageHistory;
    public static String PreferencePage_ES_Index;
    public static String PreferencePage_ES_Mapping;
    public static String PreferencePage_ES_URL;
    public static String PreferencePage_JMS_Pass;
    public static String PreferencePage_JMS_Topic;
    public static String PreferencePage_JMS_URL;
    public static String PreferencePage_JMS_User;
    public static String PreferencePage_Starttime;
    public static String Property;
    public static String PropertyValue_TTFmt;
    public static String ShowDetail;
    public static String TableColumnsEditor_Columns;
    public static String TableColumnsEditor_Title;
    public static String TableColumnsEditor_TT;
    public static String Value;

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
