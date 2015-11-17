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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
    public void testSetCommand()
    {
        SetCommand command = new SetCommand("device", 3.14, false, "nothing", false, 0.1, 0.0);
        System.out.println(command);
        assertThat(command.toString(), equalTo("Set 'device' = 3.14"));

        command = new SetCommand("device", 3.14, false, "readback", true, 0.1, 10.0);
        System.out.println(command);
        assertThat(command.toString(), equalTo("Set 'device' = 3.14 (wait for 'readback' +-0.1, 10.0 sec timeout)"));

        command = new SetCommand("device", 3.14, true, "nothing", false, 0.1, 10.0);
        System.out.println(command);
        assertThat(command.toString(), equalTo("Set 'device' = 3.14 with completion in 10.0 sec"));

        command = new SetCommand("device", 3.14, true, "readback", true, 0.1, 10.0);
        System.out.println(command);
        assertThat(command.toString(), equalTo("Set 'device' = 3.14 with completion in 10.0 sec (check for 'readback' +-0.1)"));
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

        command.setCompletion(true);
        System.out.println(command);
        assertThat(command.toString(), equalTo("Loop 'device' = 1.0 ... 10.0, step 0.5 with completion in 2.0 sec"));

        command.setWait(true);
        System.out.println(command);
        assertThat(command.toString(), equalTo("Loop 'device' = 1.0 ... 10.0, step 0.5 with completion in 2.0 sec (check for 'readback' +-0.05)"));
    }
}
