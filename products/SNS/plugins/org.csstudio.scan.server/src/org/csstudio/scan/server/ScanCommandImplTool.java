/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
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
package org.csstudio.scan.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.command.ScanCommand;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

/** Tool that creates executable command implementations of a {@link ScanCommand}.
 *
 *  <p>Queries the plugin registry for the {@link ScanCommandImplFactory}
 *  of each {@link ScanCommandImpl} to create the associated  {@link ScanCommand}.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandImplTool
{
    private static ScanCommandImplTool instance = null;
    final private Map<String, ScanCommandImplFactory<ScanCommand>> factories =
            new HashMap<String, ScanCommandImplFactory<ScanCommand>>();

    /** @return Singleton instance
     *  @throws Exception on error creating the initial instance
     */
    public static synchronized ScanCommandImplTool getInstance() throws Exception
    {
        if (instance == null)
            instance = new ScanCommandImplTool();
        return instance;
    }

    /** Initialize from extension point registry
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
    private ScanCommandImplTool() throws Exception
    {
        // Locate all commands
    	final IExtensionRegistry registry = RegistryFactory.getRegistry();
        final IExtensionPoint point = registry.getExtensionPoint("org.csstudio.scan.server.scancommandimpl");
        final IConfigurationElement[] configs = point.getConfigurationElements();

        // System.out.println("Available Scan Commands:");
        for (IConfigurationElement config : configs)
        {
            // final String plugin_id = config.getContributor().getName();

            // Instantiate factory
            final String command_class = config.getAttribute("command");
            final ScanCommandImplFactory<ScanCommand> factory = (ScanCommandImplFactory<ScanCommand>) config.createExecutableExtension("factory");
            factories.put(command_class, factory);

            // System.out.println(command_class
            //         + " (factory " + factory.getClass().getName() + ") provided by"
            //         + plugin_id);
        }
    }

    /** Get implementation
     *  @param command Command description
     *  @return Implementation
     *  @throws Exception if command lacks an implementation
     */
    public ScanCommandImpl<?> implement(final ScanCommand command) throws Exception
    {
        final String command_class = command.getClass().getName();
        final ScanCommandImplFactory<ScanCommand> factory = factories.get(command_class);
        if (factory == null)
            throw new Exception("Unknown command " + command.getClass().getName());
        return factory.createImplementation(command);
    }

    /** Get implementations
     *  @param commands Command descriptions
     *  @return Implementations
     *  @throws Exception if a command lacks an implementation
     */
    public List<ScanCommandImpl<?>> implement(final List<ScanCommand> commands) throws Exception
    {
        final List<ScanCommandImpl<?>> impl = new ArrayList<ScanCommandImpl<?>>(commands.size());
        for (ScanCommand command : commands)
            impl.add(implement(command));
        return impl;
    }
}
