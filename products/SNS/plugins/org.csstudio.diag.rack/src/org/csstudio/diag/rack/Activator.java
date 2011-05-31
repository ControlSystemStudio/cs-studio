/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends AbstractUIPlugin
{
    /** Plugin ID registered in MANIFEST.MF */
    final private static String ID = "org.csstudio.diag.rack";

    /** Logger */
    final private static Logger logger = Logger.getLogger(ID);

    private static Activator plugin = null;

    public Activator()
    {
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /** @return Logger for plugin ID */
    public static Logger getLogger()
    {
        return logger;
    }

    public static Activator getDefault()
    {
        return plugin;
    }
}
