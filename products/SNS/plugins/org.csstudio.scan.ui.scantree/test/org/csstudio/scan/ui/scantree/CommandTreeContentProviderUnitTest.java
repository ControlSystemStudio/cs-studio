/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.WaitCommand;
import org.junit.Test;

/** JUnit test of the {@link CommandTreeContentProvider}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandTreeContentProviderUnitTest
{
    @Test
    public void testCommandTreeContentProvider()
    {
        final List<ScanCommand> commands = DemoScan.createCommands();

        final CommandTreeContentProvider provider = new CommandTreeContentProvider();
        provider.inputChanged(null, null, commands);

        // Compare basic list of commands
        final List<ScanCommand> elements = provider.getChildren(commands);
        assertSame(commands, elements);

        // Check for loops
        assertTrue(elements.get(3) instanceof LoopCommand);
        assertTrue(elements.get(4) instanceof LoopCommand);
        LoopCommand loop = (LoopCommand) elements.get(3);

        // Loop body turns into child elements in tree display
        List<ScanCommand> body = provider.getChildren(loop);
        assertNotNull(body);

        assertEquals(loop.getBody().size(), body.size());
        assertEquals(loop, provider.getParent(body.get(0)));

        // Check body of 2D loop
        loop = (LoopCommand) elements.get(4);
        body = provider.getChildren(loop);
        assertNotNull(body);
        // Get inner loop
        assertNotNull(provider.getChildren(body.get(0)));
        loop = (LoopCommand) body.get(0);
        assertEquals("ypos", loop.getDeviceName());
        body = provider.getChildren(loop);

        assertTrue(body.get(0) instanceof WaitCommand);
        assertEquals(loop, provider.getParent(body.get(0)));
    }
}
