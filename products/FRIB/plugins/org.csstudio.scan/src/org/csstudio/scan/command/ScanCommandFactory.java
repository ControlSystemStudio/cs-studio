/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.command;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.RegistryFactory;

/** Implementation of the {@link SimpleScanCommandFactory} for Eclipse code
 *
 *  <p>Utilizes the extension point registry.
 *
 *  @see SimpleScanCommandFactory
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandFactory extends SimpleScanCommandFactory
{
    final public static String COMMAND_EXT_POINT = "org.csstudio.scan.command";

    /** Create a {@link ScanCommand} for a command ID
     *
     *  <p>This is the derived implementation that uses the Eclipse registry.
     *  Non-Eclipse code that does not have access to the Eclipse registry
     *  should use the {@link SimpleScanCommandFactory}
     *
     *  @param id ID of the command
     *  @return ScanCommand
     *  @throws Exception on error
     */
    @Override
    public ScanCommand createCommandForID(final String id) throws Exception
    {
    	// Locate registered commands
        final IExtensionPoint point = RegistryFactory.getRegistry().getExtensionPoint(COMMAND_EXT_POINT);
        final IConfigurationElement[] configs = point.getConfigurationElements();
        // Find command with matching ID
        for (IConfigurationElement config : configs)
        {
            if (id.equals(config.getAttribute("id")))
                return (ScanCommand) config.createExecutableExtension("class");
        }
        throw new Exception("Unknown command type '" + id + "'");
    }
}
