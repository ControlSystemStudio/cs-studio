/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

/** Factory for scan {@link DataLog}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DataLogFactory
{
	/** Plugin ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.scan.log";

    private static IDataLogFactory factory = null;

    /** @return IDataLogFactory from registry or default (mem log) */
    private static synchronized IDataLogFactory getDataLogFactory() throws Exception
    {
    	if (factory == null)
    	{
        	// Is registry available?
        	final IExtensionRegistry registry = RegistryFactory.getRegistry();
        	if (registry != null)
        	{	// Locate 'plugged' data logger
        		final IConfigurationElement[] configs = registry.getConfigurationElementsFor("org.csstudio.scan.log.datalog");
        		// Allow only one
        		if (configs.length > 1)
        		{
        			final Logger logger = Logger.getLogger(DataLogFactory.class.getName());
    				logger.log(Level.SEVERE, "Found multiple data loggers:");
    				for (IConfigurationElement config : configs)
    					logger.log(Level.SEVERE, config.getContributor().getName() + " - " + config.getAttribute("class") + " [" + config.getAttribute("name") + "]");
        			throw new Exception("Found multiple scan data loggers");
        		}
        		else if (configs.length == 1)
        			factory = (IDataLogFactory) configs[0].createExecutableExtension("class");
        	}

        	// Fall back to default logger
        	if (factory == null)
        		factory = new MemoryDataLogFactory();
    	}
    	return factory;
    }

    /** {@inheritDoc} */
    public static long createDataLog(final String scan_name) throws Exception
    {
		return getDataLogFactory().createDataLog(scan_name);
    }

    /** {@inheritDoc} */
    public static DataLog getDataLog(final long scan_id) throws Exception
    {
		return getDataLogFactory().getDataLog(scan_id);
    }
}
