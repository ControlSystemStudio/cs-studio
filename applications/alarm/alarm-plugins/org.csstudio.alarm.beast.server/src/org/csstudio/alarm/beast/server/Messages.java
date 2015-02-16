/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import org.eclipse.osgi.util.NLS;

/** Access to extenalized strings.
 *  @author Eclipse Externalization Wizard
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.server.messages"; //$NON-NLS-1$

    public static String AlarmMessageDisabled;
    public static String AlarmMessageDisconnected;
    public static String AlarmMessageNotConnected;
    public static String StartupMessage;

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
