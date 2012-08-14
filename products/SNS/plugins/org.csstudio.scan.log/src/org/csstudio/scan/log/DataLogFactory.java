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

import org.csstudio.scan.server.Scan;
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

    /** Create new log for a new scan
     *  @param scan_name Name of the scan (doesn't need to be unique)
     *  @return Scan with ID that can now and later be used to access the data log
     *  @throws Exception on error
     */
    public static Scan createDataLog(final String scan_name) throws Exception
    {
		final Scan scan = getDataLogFactory().createDataLog(scan_name);
		return scan;
    }

    /** Obtain all available scans
     *  @return Scans that have been logged
     *  @throws Exception on error
     */
    public static Scan[] getScans() throws Exception
    {
        return getDataLogFactory().getScans();
    }

    /** Get log for a scan
     *  @param scan Scan
     *  @return DataLog for Scan
     *  @throws Exception on error
     */
    public static DataLog getDataLog(final Scan scan) throws Exception
    {
		return getDataLogFactory().getDataLog(scan);
    }

    /** Delete logged data for a scan
     *  @param scan Scan
     *  @throws Exception on error
     */
    public static void deleteDataLog(final Scan scan) throws Exception
    {
        getDataLogFactory().deleteDataLog(scan);
    }
}
