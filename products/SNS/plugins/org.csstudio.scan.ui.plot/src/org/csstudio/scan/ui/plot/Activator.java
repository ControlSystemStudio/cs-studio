/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** IS defined in MANIFEST.MF */
    public static final String PLUGIN_ID = "org.csstudio.scan.ui.plot";

    /** Singleton instance */
    private static Activator plugin;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }

    /** @return Singleton instance */
    public static Activator getDefault()
    {
        return plugin;
    }

}
