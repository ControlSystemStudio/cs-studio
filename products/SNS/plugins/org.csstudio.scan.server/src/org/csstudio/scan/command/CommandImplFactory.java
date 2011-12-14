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
package org.csstudio.scan.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/** Factory that creates executable command implementations
 *  from command descriptions sent by a client.
 *  
 *  <p>Locates the {@link CommandImpl} for a {@link ScanCommand}
 *  based on the class name of the command via introspection.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandImplFactory
{
    /** Get implementation
     *  @param command Command description
     *  @return Implementation
     *  @throws Exception if command lacks an implementation
     */
    public static CommandImpl implement(final ScanCommand command) throws Exception
    {
        // Is the passed command already an implementation?
        if (command instanceof CommandImpl)
            return (CommandImpl) command;
        
        // Convert
        //   org.csstudio.scan.command.LogCommand
        // into
        //   org.csstudio.scan.command.LogCommandImpl
        final String class_name = command.getClass().getName() + "Impl";
        try
        {
            // Find a constructor that takes the original ScanCommand as parameter
            Constructor<?>[] constructors = Class.forName(class_name).getConstructors();
            for (Constructor<?> c : constructors)
            {
                Class<?>[] parms = c.getParameterTypes();
                if (parms.length == 1  &&
                    parms[0] == command.getClass())
                {
                    return (CommandImpl) c.newInstance(command);
                }
            }
        }
        catch (Throwable ex)
        {
            throw new Exception("Error implementing " + command.getClass().getName(), ex);
        }

        throw new Exception("Unknown command " + command.getClass().getName());
    }

    /** Get implementations
     *  @param commands Command descriptions
     *  @return Implementations
     *  @throws Exception if a command lacks an implementation
     */
    public static List<CommandImpl> implement(final List<ScanCommand> commands) throws Exception
    {
        final List<CommandImpl> impl = new ArrayList<CommandImpl>(commands.size());
        for (ScanCommand command : commands)
            impl.add(CommandImplFactory.implement(command));
        return impl;
    }
}
