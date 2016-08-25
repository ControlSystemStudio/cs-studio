/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.csstudio.vtype.pv.PV.logger;

import java.util.logging.Level;

import org.csstudio.vtype.pv.internal.Preferences;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVPlugin implements BundleActivator
{
    final public static String ID = "org.csstudio.vtype.pv";

    @Override
    public void start(final BundleContext context) throws Exception
    {
        // Configure PVPool from Eclipse registry
        final IConfigurationElement[] configs =
                Platform.getExtensionRegistry().getConfigurationElementsFor(PVFactory.EXTENSION_POINT);
        if (configs.length < 1)
            throw new Exception("No PVFactories");
        for (IConfigurationElement config : configs)
        {
            final String plugin = config.getContributor().getName();
            final String name = config.getAttribute("name");
            try
            {
                final PVFactory factory = (PVFactory) config.createExecutableExtension("class");
                logger.log(Level.CONFIG, "PV prefix {0} provided by {1} in {2}",
                    new Object[] { factory.getType(), name, plugin });
                PVPool.addPVFactory(name, factory);
            }
            catch (Exception ex)
            {
                logger.log(Level.SEVERE,
                           "Failed to initialize PV type '" + name + "' from " + plugin,
                           ex);
            }
        }

        // Set default type after adding factories
        // (otherwise factory added last would be the default)
        final String default_type = Preferences.defaultType();
        logger.log(Level.CONFIG, "Default PV type {0}", default_type);
        PVPool.setDefaultType(default_type);
    }

    @Override
    public void stop(final BundleContext context) throws Exception
    {
        // NOP
    }
}
