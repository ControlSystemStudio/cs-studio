/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.ui;

import org.eclipse.osgi.util.NLS;

/** Localized Texts
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.logging.ui.messages"; //$NON-NLS-1$

    public static String ConsoleView_Title;
    public static String FileCount;
    public static String FileLevel;
    public static String FileLogSettings;
    public static String FilePathPattern;
    public static String FileSize;
    public static String GlobalLevel;
    public static String GlobalSettings;
    public static String JMSLevel;
    public static String JMSLogSettings;
    public static String JMSTopic;
    public static String JMSURL;
    public static String LogConfigError;
    public static String MessageDetail;
    public static String PrefPageTitle;

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
