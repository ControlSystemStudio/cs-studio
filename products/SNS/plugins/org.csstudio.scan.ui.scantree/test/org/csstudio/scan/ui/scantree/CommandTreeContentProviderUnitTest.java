/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
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
        final Object[] elements = provider.getElements(commands);
        assertEquals(commands.size(), elements.length);
        assertSame(commands.get(0), elements[0]);
        
        // Check for loops
        assertTrue(elements[3] instanceof LoopCommand);
        assertTrue(elements[4] instanceof LoopCommand);
        LoopCommand loop = (LoopCommand) elements[3];
        
        // Loop body turns into child elements in tree display
        assertTrue(provider.hasChildren(loop));
        Object[] body = provider.getChildren(loop);
        
        assertEquals(loop.getBody().size(), body.length);
        assertEquals(loop, provider.getParent(body[0]));
        
        // Check body of 2D loop
        loop = (LoopCommand) elements[4];
        assertTrue(provider.hasChildren(loop));
        body = provider.getChildren(loop);
        // Get inner loop
        assertTrue(provider.hasChildren(body[0]));
        loop = (LoopCommand) body[0];
        assertEquals("ypos", loop.getDeviceName());
        body = provider.getChildren(loop);
        
        assertTrue(body[0] instanceof LogCommand);
        assertEquals(loop, provider.getParent(body[0]));
    }
}
