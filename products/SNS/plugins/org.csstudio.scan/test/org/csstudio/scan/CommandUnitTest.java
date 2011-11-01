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

import java.io.PrintStream;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitForValueCommand;
import org.junit.Test;


/** JUnit test of the Command printout
 *  @author Kay Kasemir
 */
public class CommandUnitTest
{
    /** Very fragile line counter,
     *  just good enough for the test
     */
    static private class CountingPrintStream extends PrintStream
    {
        int lines = 0;

        public CountingPrintStream()
        {
            super(System.out);
        }

        @Override
        public void println(final String s)
        {
            ++lines;
            super.println(s);
        }
    }

    @Test
    public void testCommands()
    {
        final ScanCommand command = new LoopCommand("outer", 1, 5, 1,
                new LogCommand("outer"),
                new LoopCommand("inner", 1, 10, 2,
                        new SetCommand("setpoint", 1),
                        new WaitForValueCommand("readback", 1.0, 0.1),
                        new DelayCommand(0.5),
                        new LogCommand("inner", "readback")));

        final CountingPrintStream printer = new CountingPrintStream();
        command.print(printer);
        assertEquals(7, printer.lines);
    }
}
