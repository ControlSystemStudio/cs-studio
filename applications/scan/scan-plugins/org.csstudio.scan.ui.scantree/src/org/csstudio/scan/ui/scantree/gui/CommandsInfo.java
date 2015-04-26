/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.command.ScanCommand;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Info about all available commands
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandsInfo
{
    /** Singleton instance */
    private static CommandsInfo instance = null;

    /** Default instances for each available command */
    final private ScanCommand[] commands;

    /** Icon for each command, indexed by name of command class */
    final private ImageRegistry registry = new ImageRegistry();

    /** GUI name for each command, indexed by command class */
    final private Map<Class<?>, String> command_names = new HashMap<Class<?>, String>();

    /** @return Singleton instance
     *  @throws Exception on error creating the initial instance
     */
    public static synchronized CommandsInfo getInstance() throws Exception
    {
        if (instance == null)
            instance = new CommandsInfo();
        return instance;
    }

    /** Initialize from extension point registry */
    private CommandsInfo() throws Exception
    {
        // Locate all commands
        final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.csstudio.scan.command");
        final IConfigurationElement[] configs = point.getConfigurationElements();

        final List<ScanCommand> commands = new ArrayList<ScanCommand>();
        for (IConfigurationElement config : configs)
        {
            final String plugin_id = config.getContributor().getName();
            final String name = config.getAttribute("name");
            final String icon_path = config.getAttribute("icon");

            // Instantiate command
            final ScanCommand command = (ScanCommand) config.createExecutableExtension("class");
            commands.add(command);

            command_names.put(command.getClass(), name);

            // Get icon
            final ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(plugin_id, icon_path);
            registry.put(command.getClass().getName(), icon);
        }
        // Sort by command class name to get predicatable order
        Collections.sort(commands, new Comparator<ScanCommand>()
		{
			@Override
            public int compare(final ScanCommand cmd1, final ScanCommand cmd2)
            {
	            return cmd1.getCommandName().compareTo(cmd2.getCommandName());
            }
		});
        this.commands = commands.toArray(new ScanCommand[commands.size()]);
    }

    /** @return All available commands */
    public ScanCommand[] getCommands()
    {
        return commands;
    }

    /** Get icon for commmand
     *  @param command Command
     *  @return Associated icon. Caller must NOT dispose.
     */
    public Image getImage(final ScanCommand command)
    {
        final String command_name = command.getClass().getName();
        return registry.get(command_name);
    }

    /** Get GUI name for commmand
     *  @param command Command
     *  @return Associated GUI name
     */
    public String getName(final ScanCommand command)
    {
        final String command_name = command_names.get(command.getClass());
        if (command_name != null)
            return command_name;
        return "Scan Command";
    }

}
