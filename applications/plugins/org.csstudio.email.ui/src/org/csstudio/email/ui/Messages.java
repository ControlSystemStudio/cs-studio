/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email.ui;

import org.eclipse.osgi.util.NLS;

/** Externalized Strings
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.email.ui.messages"; //$NON-NLS-1$

    public static String DefaultDestination;
    public static String EmailDialogMessage;
    public static String From;

    public static String FromErrorMsg;
    public static String FromTT;
    public static String MessageBodyTT;
    public static String Preferences;
    public static String SendEmail;
    public static String SMTPHost;
    public static String Subject;
    public static String SubjectTT;
    public static String To;

    public static String ToErrorMsg;
    public static String ToTT;

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
