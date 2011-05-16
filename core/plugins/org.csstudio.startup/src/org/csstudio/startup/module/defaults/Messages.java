/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.startup.module.defaults;

import org.eclipse.osgi.util.NLS;

/** Localized messages
 *  @author IDE
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.startup.module.defaults.messages"; //$NON-NLS-1$
    public static String CloseProjectErrorFmt;
    public static String CreateProjectErrorFmt;
    public static String DefaultProjectName;
    public static String Error;
    public static String OpenProjectErrorFmt;

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
