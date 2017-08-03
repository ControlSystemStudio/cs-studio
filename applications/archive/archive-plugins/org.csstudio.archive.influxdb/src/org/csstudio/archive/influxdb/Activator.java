/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.influxdb;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** (Not really a) Plugin Activator
 *  @author Megan Grodowitz
 */
public class Activator
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.archive.influxdb"; //$NON-NLS-1$

    private static Activator instance = new Activator();
    private final Logger logger;

    /** @return Singleton instance */
    public static Activator getInstance()
    {
        return instance;
    }

    Activator()
    {
        logger = Logger.getLogger(ID);
        logger.setLevel(Level.FINE);
        for (Handler handler : logger.getHandlers())
        {
            handler.setLevel(Level.FINE);
        }
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);
    }

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return instance.logger;
    }

}
