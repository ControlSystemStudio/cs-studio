/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.logbook.ui.messages"; //$NON-NLS-1$
    public static String Error;
    public static String LogEntry_ErrorCannotConnectFMT;
    public static String LogEntry_ErrorFMT;
    public static String LogEntry_ErrorNoLogFMT;
    public static String LogEntry_Logbook;
    public static String LogEntry_Logbook_TT;
    public static String LogEntry_Password;
    public static String LogEntry_Password_TT;
    public static String LogEntry_Submit;
    public static String LogEntry_Submit_TT;
    public static String LogEntry_Text;
    public static String LogEntry_Text_TT;
    public static String LogEntry_Title;
    public static String LogEntry_Title_TT;
    public static String LogEntry_User;
    public static String LogEntry_User_TT;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // NOP
    }
}
