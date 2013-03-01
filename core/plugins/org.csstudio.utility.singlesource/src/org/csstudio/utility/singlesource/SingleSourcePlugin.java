/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/** API for obtaining single-source helper
 * 
 *  <p>Acts as plugin activator
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SingleSourcePlugin implements BundleActivator
{
    /** Extension point ID for providing the helpers */
    final public static String EXT_ID = "org.csstudio.utility.singlesource.helpers";
    
    private static ResourceHelper resources;
    
    private static UIHelper ui;

    /** {@inheritDoc} */
    public void start(final BundleContext context) throws Exception
	{
	    // Registry lookup
	    final IExtensionRegistry registry = RegistryFactory.getRegistry();
	    final IConfigurationElement[] configs = registry.getConfigurationElementsFor(EXT_ID);
	    if (configs.length > 1)
	        throw new Exception("Found " + configs.length +
	                " Single Source Helper implementations, expecting at most one");
	    if (configs.length == 1)
	    {   // Use implementations from extension point
	        Logger.getLogger(getClass().getName()).config("ResourceHelper provided by " + configs[0].getContributor().getName());
	        SingleSourcePlugin.resources = (ResourceHelper)
	            configs[0].createExecutableExtension("resources");
            SingleSourcePlugin.ui = (UIHelper)
                    configs[0].createExecutableExtension("ui");
	    }
	    else
	    {   // Use default implementations
	        SingleSourcePlugin.resources = new ResourceHelper();
	        SingleSourcePlugin.ui = new UIHelper();
	    }
	}

	/** {@inheritDoc} */
	public void stop(final BundleContext context) throws Exception
	{
	    SingleSourcePlugin.resources = null;
	}
	
	/** @return {@link ResourceHelper} */
	public static ResourceHelper getResourceHelper()
	{
	    return SingleSourcePlugin.resources;
	}

    /** @return {@link UIHelper} */
    public static UIHelper getUIHelper()
    {
        return SingleSourcePlugin.ui;
    }
}
