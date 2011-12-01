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

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitForValueCommand;

/** Factory that creates executable command implementations
 *  from command descriptions sent by a client
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
        if (command instanceof DelayCommand)
            return new DelayCommandImpl((DelayCommand) command);

        if (command instanceof LogCommand)
            return new LogCommandImpl((LogCommand) command);

        if (command instanceof LoopCommand)
            return new LoopCommandImpl((LoopCommand) command);

        if (command instanceof SetCommand)
            return new SetCommandImpl((SetCommand) command);

        if (command instanceof WaitForValueCommand)
            return new WaitForValueCommandImpl((WaitForValueCommand) command);

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
