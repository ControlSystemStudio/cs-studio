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
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.junit.Test;

/** JUnit test of the Command printout
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandSequenceUnitTest
{
    @Test
    public void testCommandSequence()
    {
    	final CommandSequence commands = new CommandSequence();
    	// Add commands
    	commands.add(new SetCommand("setpoint", 1));
    	commands.add(new WaitCommand("readback", 1.0, 0.1));
    	// Add same commands via shortcut
    	commands.set("setpoint", 1);
    	commands.wait("readback", 1.0, 0.1);
    	commands.dump();
    	// Check the list
    	final List<ScanCommand> list = commands.getCommands();
        assertEquals(4, list.size());
        assertEquals(list.get(0).getClass(), list.get(2).getClass());
        assertEquals(list.get(1).getClass(), list.get(3).getClass());
    }
}
