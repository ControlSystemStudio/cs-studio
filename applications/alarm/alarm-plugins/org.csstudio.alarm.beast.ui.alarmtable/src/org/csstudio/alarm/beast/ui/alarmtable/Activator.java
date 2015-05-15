/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Plug-in ID defined in MANIFEST.MF */
    public static final String ID = "org.csstudio.alarm.beast.ui.alarmtable"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator plugin;

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        setPlugin(this);
    }

    /** Static setter to avoid FindBugs warning */
    private static void setPlugin(final Activator the_plugin)
    {
        plugin = the_plugin;
    }

    /** @eturn The shared instance. */
    public static Activator getDefault()
    {
        return plugin;
    }
}
