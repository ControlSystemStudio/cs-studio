/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.csstudio.scan.server.SimulationContext;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the scan command simulation
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanSimulationHeadlessTest
{
    @Test
    public void testImplementScanCommand() throws Exception
    {
        final List<ScanCommand> commands = Arrays.asList( (ScanCommand)
        		new DelayCommand(60.0),
        		new SetCommand("shutter", 1.0),
        		new SetCommand("shutter", 0.0),
        		new SetCommand("shutter", 1.0),
        		new SetCommand("setpoint", 1.0, "readback"),
        		new SetCommand("setpoint", 10.0, "readback"),
        		new LoopCommand("xpos", 0, 10, 5,
        			new LoopCommand("ypos", 0, 10, 5,
        				new WaitCommand("neutrons", Comparison.INCREASE_BY, 3.0)
        			)
        		)
    		);

        final ScanCommandImplTool tool = ScanCommandImplTool.getInstance();
        List<ScanCommandImpl<?>> scan = tool.implement(commands, null);

        ByteArrayOutputStream simu_log = new ByteArrayOutputStream();
        final SimulationContext context = new SimulationContext(new PrintStream(simu_log));
        context.simulate(scan);
        final String log_text = simu_log.toString();

        System.out.println("--------");
        System.out.println("Simulation:");
        System.out.println("--------");
		System.out.print(log_text);
        System.out.println("--------");
        System.out.println(context.getSimulationTime() + "   Total estimated execution time");

        assertTrue(log_text.length() > 0);
        assertEquals(9*60+47, context.getSimulationSeconds(), 10);
    }
}
