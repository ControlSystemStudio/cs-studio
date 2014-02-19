/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.vtype.pv.PVFactory;
import org.csstudio.vtype.pv.PVPool;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator implements BundleActivator
{
    final public static String ID = "org.csstudio.utility.pv.vtype";
    
    @Override
    public void start(final BundleContext context) throws Exception
    {
        final Logger logger = Logger.getLogger(getClass().getName());
        
        // Configure PVPool from Eclipse registry
        final IConfigurationElement[] configs =
                Platform.getExtensionRegistry().getConfigurationElementsFor(PVFactory.EXTENSION_POINT);
        if (configs.length < 1)
            throw new Exception("No PVFactories");
        for (IConfigurationElement config : configs)
        {
            final String plugin = config.getContributor().getName();
            final String name = config.getAttribute("name");
            final PVFactory factory = (PVFactory) config.createExecutableExtension("class");
            logger.log(Level.CONFIG, "PV prefix {0} provided by {1} in {2}",
                new Object[] { factory.getType(), name, plugin });
            PVPool.addPVFactory(factory);
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
