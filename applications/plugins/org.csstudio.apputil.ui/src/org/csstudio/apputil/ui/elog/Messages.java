/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.elog;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.apputil.ui.elog.messages"; //$NON-NLS-1$
    public static String ELog_ActionName;
    public static String ELog_ActionName_TT;
    public static String ELog_Dialog_Body;
    public static String ELog_Dialog_Body_TT;
    public static String ELog_Dialog_DialogTitle;
    public static String ELog_Dialog_Logbook;
    public static String ELog_Dialog_Logbook_TT;
    public static String ELog_Dialog_Password;
    public static String ELog_Dialog_Password_TT;
    public static String ELog_Dialog_Title;
    public static String ELog_Dialog_Title_TT;
    public static String ELog_Dialog_User;
    public static String ELog_Dialog_User_TT;
    public static String ELog_Dialog_WindowTitle;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    { /* prevent instantiation */ }
}
