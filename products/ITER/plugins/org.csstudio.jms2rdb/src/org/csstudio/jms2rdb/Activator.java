/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 *  reviewed by Katia Danilova 08/20/08
 */
@SuppressWarnings("nls")
public class Activator extends Plugin
{
    /** The plug-in ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.jms2rdb";

    final private static Logger logger = Logger.getLogger(ID);

    /** Singleton instance */
    private static Activator instance;

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        instance = null;
        super.stop(context);
    }

    /** @return Singleton instance */
    public static Activator getInstance()
    {
        return instance;
    }

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }
}
