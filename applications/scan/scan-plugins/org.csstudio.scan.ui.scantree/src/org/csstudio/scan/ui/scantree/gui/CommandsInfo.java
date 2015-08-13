/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final static CommandsInfo instance = new CommandsInfo();

    /** Default instances for each available command */
    private final ScanCommand[] commands;

    /** Icon for each command, indexed by name of command class */
    private final ImageRegistry registry = new ImageRegistry();

    /** GUI name for each command, indexed by command class */
    private final Map<Class<?>, String> command_names = new HashMap<>();

    /** @return Singleton instance */
    public static CommandsInfo getInstance()
    {
        return instance;
    }

    /** Initialize from extension point registry */
    private CommandsInfo()
    {
        // Locate all commands
        final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.csstudio.scan.command");
        final IConfigurationElement[] configs = point.getConfigurationElements();

        final List<ScanCommand> commands = new ArrayList<>();
        for (IConfigurationElement config : configs)
        {
            final String plugin_id = config.getContributor().getName();
            final String name = config.getAttribute("name");
            final String icon_path = config.getAttribute("icon");

            try
            {
                // Instantiate command
                final ScanCommand command = (ScanCommand) config.createExecutableExtension("class");
                commands.add(command);
                command_names.put(command.getClass(), name);

                // Get icon
                final ImageDescriptor icon = AbstractUIPlugin.imageDescriptorFromPlugin(plugin_id, icon_path);
                registry.put(command.getClass().getName(), icon);
            }
            catch (Exception ex)
            {
                Logger.getLogger(getClass().getName())
                      .log(Level.SEVERE, "Cannot initialize command '" + name + " from " + plugin_id, ex);
            }
        }
        // Sort by command class name to get predictable order
        commands.sort((cmd1, cmd2) -> cmd1.getCommandName().compareTo(cmd2.getCommandName()));
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
