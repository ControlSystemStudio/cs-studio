/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;

/**
 * The activator class controls the plug-in life cycle
 *
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *
 *    reviewed by Delphy 01/28/09
 */
public class Activator extends Plugin
{
    /** Plug-in ID, defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.display.pace"; //$NON-NLS-1$

    final private static Logger logger = Logger.getLogger(ID);

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }
}
