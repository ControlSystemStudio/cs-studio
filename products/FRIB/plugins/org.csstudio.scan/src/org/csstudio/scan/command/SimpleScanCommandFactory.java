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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Factory for creating scan commands.
 *
 *  <p>{@link ScanCommand}s are used in two different
 *  scenarios.
 *
 *  <p>Within Eclipse, scan commands are contributed via
 *  extension points.
 *  The code that needs a command does not necessarily have
 *  access to the scan command classes via its own class loader.
 *  The extension point registry can create instances of commands
 *  that are contributed by other plugins, even if we do not have
 *  a dependency on that plugin and thus no access to its class loader.
 *
 *  <p>On the other hand, we also want to provide access to the scan system
 *  outside of Eclipse, for example from jython scripts or other
 *  Java-aware tools like Matlab.
 *  Such code need to have access to all the desired ScanCommand types via
 *  their classpath, so that a plain class loader can create
 *  instances.
 *
 *  <p>This is the base implementation that creates {@link ScanCommand}s
 *  via the class loader.
 *  Eclipse-aware code uses a derived version which utilizes
 *  the registry.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimpleScanCommandFactory
{
    /** Create a {@link ScanCommand} for a command ID
     *
     *  <p>This is the basic implementation that does NOT use the Eclipse registry.
     *  Eclipse code should use the derived {@link ScanCommandFactory}
     *
     *  @param id ID of the command
     *  @return ScanCommand
     *  @throws Exception on error
     */
    public ScanCommand createCommandForID(final String id) throws Exception
    {
        // Guess class name based on the ID: Turn ID "set" into "....SetCommand"
        final String classname = "org.csstudio.scan.command." +
                id.substring(0, 1).toUpperCase() +
                id.substring(1).toLowerCase() + "Command";
        try
        {
            final Class<?> command_class = Class.forName(classname);
            return (ScanCommand) command_class.getConstructor().newInstance();
        }
        catch (Throwable ex)
        {
            throw new Exception("Unknown command type '" + id + "'");
        }
    }

    /** Read a list of commands (and possible sub-commands like loop bodies)
     *  @param node Node and siblings that contain commands
     *  @return List of {@link ScanCommand}s
     *  @throws Exception on error: Unknown commands, missing command-specific details
     */
    final public List<ScanCommand> readCommands(Node node) throws Exception
    {
        final List<ScanCommand> commands = new ArrayList<ScanCommand>();
        while (node != null)
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
                commands.add(readCommand((Element) node));
            node = node.getNextSibling();
        }
        return commands;
    }

    /** Read a ScanCommand from an XML element.
     *
     *  @param element XML element. Name of the element determines the ScanCommand
     *  @return {@link ScanCommand}
     *  @throws Exception on error: Unknown command, missing command-specific detail
     */
    final public ScanCommand readCommand(final Element element) throws Exception
    {
        // Guess class name based on the ID
        final String id = element.getNodeName();
        final ScanCommand command = createCommandForID(id);
        command.readXML(this, element);
        return command;
    }
}
