/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Megan Grodowitz
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    /** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.archive.reader.influxdb.raw";

    private static Activator instance = new Activator();
    // private final Logger logger;

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    /** @return Singleton instance */
    public static Activator getInstance()
    {
        return instance;
    }

    // Activator()
    // {
    // logger = Logger.getLogger(ID);
        //        logger.setLevel(Level.FINE);
        //        for (Handler handler : logger.getHandlers())
        //        {
        //            handler.setLevel(Level.FINE);
        //        }
        //        ConsoleHandler handler = new ConsoleHandler();
        //        // PUBLISH this level
        //        handler.setLevel(Level.FINE);
        //        logger.addHandler(handler);
    // }

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        // return instance.logger;
        return Logger.getLogger(ID);
    }
}
