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

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;

/** Create Commands for tests
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DemoCommands
{
    public static List<ScanCommand> createDemoCommands()
    {
    	final List<ScanCommand> commands = new ArrayList<ScanCommand>();
    	commands.add(new SetCommand("setpoint", 1.0));
    	commands.add(new WaitCommand("readback", Comparison.EQUALS, 1.0, 0.1, 0.0));
    	commands.add(
    	        new LoopCommand("outer", 1.0, 5.0, 1.0,
                    new LogCommand("outer"),
                    new LoopCommand("inner", 1.0, 10.0, 2.0,
                            new SetCommand("setpoint", 1.0),
                            new WaitCommand("readback", Comparison.EQUALS, 1.0, 0.1, 0.0),
                            new DelayCommand(0.5),
                            new LogCommand("inner", "readback"))));
    	return commands;
    }
}
