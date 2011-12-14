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

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.junit.Test;

/** JUnit test of scan tree manipulation
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TreeManipulatiorUnitTest
{
    @Test
    public void testTreeInsertion() throws Exception
    {
        final List<ScanCommand> commands = DemoScan.createCommands();
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertEquals(5, commands.size());
        
        assertEquals(SetCommand.class, commands.get(0).getClass());
        assertEquals(WaitCommand.class, commands.get(1).getClass());
        assertEquals(DelayCommand.class, commands.get(2).getClass());

        // Add after the second command
        ScanCommand add = new LogCommand("add_after_Wait");
        boolean ok = TreeManipulator.insertAfter(commands, commands.get(1), add);
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertTrue(ok);
        assertEquals(6, commands.size());
        assertSame(add, commands.get(2));

        // Add before the second command
        add = new LogCommand("add_before_Wait");
        ok = TreeManipulator.insertBefore(commands, commands.get(1), add);
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertTrue(ok);
        assertEquals(7, commands.size());
        assertSame(add, commands.get(1));
        
        // Add to start of loop
        assertEquals(LoopCommand.class, commands.get(5).getClass());
        List<ScanCommand> body = ((LoopCommand) commands.get(5)).getBody();
        assertTrue(body.size() > 0);
        add = new LogCommand("add_to_start_of_loop");
        ok = TreeManipulator.insertBefore(commands, body.get(0), add);
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertTrue(ok);
        // Overall size didn't change
        assertEquals(7, commands.size());
        body = ((LoopCommand) commands.get(5)).getBody();
        assertSame(add, body.get(0));
    }
    
    @Test
    public void testTreeDeletion() throws Exception
    {
        final List<ScanCommand> commands = DemoScan.createCommands();
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertEquals(5, commands.size());

        ScanCommand command = commands.get(2);
        
        // Remove second element, so next one should move 'up'
        boolean ok = TreeManipulator.remove(commands, commands.get(1));
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertTrue(ok);
        assertEquals(4, commands.size());
        assertSame(command, commands.get(1));

        // Remove from loop
        assertEquals(LoopCommand.class, commands.get(2).getClass());
        List<ScanCommand> body = ((LoopCommand) commands.get(2)).getBody();
        int body_size = body.size();
        assertTrue(body_size > 0);
        ok = TreeManipulator.remove(commands, body.get(0));
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertTrue(ok);
        // Overall size didn't change
        assertEquals(4, commands.size());
        body = ((LoopCommand) commands.get(2)).getBody();
        assertEquals(body_size-1, body.size());
    }
}
