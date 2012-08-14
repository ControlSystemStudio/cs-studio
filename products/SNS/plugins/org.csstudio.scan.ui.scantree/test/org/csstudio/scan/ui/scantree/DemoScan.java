/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.List;

import org.csstudio.scan.command.CommandSequence;
import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;

/** Demo scan, used in tests
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DemoScan
{
    /** @return Commands for demo scan */
    public static List<ScanCommand> createCommands()
    {
        // Note that
        final CommandSequence commands = new CommandSequence(
            new SetCommand("setpoint", 1.0),
            new WaitCommand("readback", 1.0),
            new DelayCommand(5.0),
            new LoopCommand("xpos", 1.0, 5.0, 1.0, new LogCommand("readback")),
            new LoopCommand("xpos", 1.0, 5.0, 1.0,
                new LoopCommand("ypos", 2.0, 4.0, 0.5,
                        new WaitCommand("setpoint", Comparison.EQUALS, 1.0, 0.1, 0.0),
                        new LogCommand("readback")))
        );
        return commands.getCommands();
    }
}
