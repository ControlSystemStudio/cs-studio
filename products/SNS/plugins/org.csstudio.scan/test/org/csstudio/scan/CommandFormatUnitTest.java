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
package org.csstudio.scan;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.SetCommand;
import org.junit.Test;

/** JUnit test of the command formatting to string
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandFormatUnitTest
{
    @Test
    public void testScanCommand()
    {
        final SetCommand command = new SetCommand("device", 3.14);
        System.out.println(command);
        assertTrue(command.toString().contains("wait"));

        command.setReadback("readback");
        System.out.println(command);
        assertTrue(command.toString().contains("readback"));

        command.setTimeout(2.0);
        System.out.println(command);
        assertTrue(command.toString().contains("timeout"));

        command.setWait(false);
        System.out.println(command);
        assertFalse(command.toString().contains("wait"));
    }

    @Test
    public void testLoopCommand()
    {
        final LoopCommand command = new LoopCommand("device", 1.0, 10.0, 0.5);
        System.out.println(command);
        assertTrue(command.toString().contains("wait"));

        command.setReadback("readback");
        System.out.println(command);
        assertTrue(command.toString().contains("readback"));

        command.setTimeout(2.0);
        System.out.println(command);
        assertTrue(command.toString().contains("timeout"));

        command.setWait(false);
        System.out.println(command);
        assertFalse(command.toString().contains("wait"));
    }

}
