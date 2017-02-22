/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Plugin class for EPICS PV Tree.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plugin extends AbstractUIPlugin
{
    /** The plug-in ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.diag.epics.pvtree"; //$NON-NLS-1$

    public final static Logger logger = Logger.getLogger(ID);

    /** The shared instance */
    private static Plugin plugin;

    /** The constructor. */
    public Plugin()
    {
        plugin = this;
    }

    /** @return Returns the shared instance. */
    public static Plugin getDefault()
    {
        return plugin;
    }

    /** @param resource_name Path to "platform:" resource
     *  @return Stream for content
     *  @throws Exception on error
     */
    public static InputStream openPlatformResource(final String resource_name) throws Exception
    {
        if (! resource_name.startsWith("platform:"))
            throw new Exception("Only handling 'platform:' path, not " + resource_name);
        try
        {
            return new URL(resource_name).openStream();
        }
        catch (Exception ex)
        {
            // Handle "platform://.." path during tests in the 'main' directory of a plugin,
            // so "../" leads to the parent of all plugin sources, from which we
            // then locate "specific_plugin/dir/file.png"
            final String resolved = resource_name.replace("platform:/plugin/", "../");
            return new FileInputStream(resolved);
        }
    }

    public static InputStream openIconStream(final String name) throws Exception
    {
        return openPlatformResource("platform:/plugin/org.csstudio.diag.epics.pvtree/icons/" + name);
    }
}
