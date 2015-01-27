/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator;

import org.eclipse.osgi.util.NLS;

/** Externalized Strings
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.annunciator.messages"; //$NON-NLS-1$

    public static String Annunciator;
    public static String Clear;
    public static String ClearTT;
    public static String ConnectMsg;
    public static String Message;
	public static String MoreMessagesFmt;
    public static String Prefs_History;
    public static String Prefs_Severities;
    public static String Prefs_Threshold;
    public static String Prefs_Topics;
    public static String Prefs_Translations;
    public static String Prefs_URL;
    public static String Severity;
    public static String Silence;
    public static String SilenceTT;
    public static String Time;

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
