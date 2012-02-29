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
import static org.junit.Assert.assertNull;
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
        assertEquals(0, TreeManipulator.getChildCount(commands.get(0)));
        assertNull(TreeManipulator.getParent(commands, commands.get(0)));

        assertEquals(WaitCommand.class, commands.get(1).getClass());
        assertEquals(DelayCommand.class, commands.get(2).getClass());

        assertEquals(LoopCommand.class, commands.get(3).getClass());
        assertEquals(1, TreeManipulator.getChildCount(commands.get(3)));

        List<ScanCommand> children = TreeManipulator.getChildren(commands.get(3));
        assertSame(commands.get(3), TreeManipulator.getParent(commands, children.get(0)));

        // Add after the second command
        ScanCommand add = new LogCommand("add_after_Wait");
        TreeManipulator.insert(commands, commands.get(1), add, true);
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertEquals(6, commands.size());
        assertSame(add, commands.get(2));

        // Add before the second command
        add = new LogCommand("add_before_Wait");
        TreeManipulator.insert(commands, commands.get(1), add, false);
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertEquals(7, commands.size());
        assertSame(add, commands.get(1));

        // Add to start of loop
        assertEquals(LoopCommand.class, commands.get(5).getClass());
        List<ScanCommand> body = ((LoopCommand) commands.get(5)).getBody();
        assertTrue(body.size() > 0);
        add = new LogCommand("add_to_start_of_loop");
        TreeManipulator.insert(commands, body.get(0), add, false);
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
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
        boolean ok = TreeManipulator.remove(commands, commands.get(1)) != null;
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
        ok = TreeManipulator.remove(commands, body.get(0)) != null;
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertTrue(ok);
        // Overall size didn't change
        assertEquals(4, commands.size());
        body = ((LoopCommand) commands.get(2)).getBody();
        assertEquals(body_size-1, body.size());
    }

    @Test
    public void testLoopAddition() throws Exception
    {
        final List<ScanCommand> commands = DemoScan.createCommands();
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertEquals(5, commands.size());

        // Add to loop
        assertEquals(LoopCommand.class, commands.get(3).getClass());
        List<ScanCommand> body = ((LoopCommand) commands.get(3)).getBody();
        final int body_size = body.size();
        ScanCommand add = new LogCommand("start_of_loop");
        TreeManipulator.addToLoop((LoopCommand)commands.get(3), add);
        XMLCommandWriter.write(System.out, commands);
        body = ((LoopCommand) commands.get(3)).getBody();
        assertEquals(body_size+1, body.size());
        assertSame(add, body.get(0));
    }

    @Test
    public void testRemoveUndo() throws Exception
    {
        final List<ScanCommand> commands = DemoScan.createCommands();
        XMLCommandWriter.write(System.out, commands);
        System.out.println("-----------------------------------------");
        assertEquals(5, commands.size());

        // Remove from start of list
        ScanCommand command = commands.get(0);
        assertSame(SetCommand.class, command.getClass());
        TreeManipulator.RemovalInfo removed = TreeManipulator.remove(commands, command);
        assertNotNull(removed);
        assertEquals(4, commands.size());

        removed.undo(commands);
        assertEquals(5, commands.size());
        assertSame(command, commands.get(0));

        // Remove from within the list
        command = commands.get(2);
        assertSame(DelayCommand.class, command.getClass());
        removed = TreeManipulator.remove(commands, command);
        assertNotNull(removed);
        assertEquals(4, commands.size());

        removed.undo(commands);
        assertEquals(5, commands.size());
        assertSame(command, commands.get(2));


        // Remove from within a nested loop
        final LoopCommand loop1 = (LoopCommand) commands.get(4);
        final LoopCommand loop2 = (LoopCommand) loop1.getBody().get(0);
        assertEquals(2, loop2.getBody().size());
        command = loop2.getBody().get(1);
        assertSame(LogCommand.class, command.getClass());
        removed = TreeManipulator.remove(commands, command);
        XMLCommandWriter.write(System.out, commands);
        assertNotNull(removed);
        // Top-level list and loop don't change, loop2 does
        assertEquals(5, commands.size());
        assertEquals(1, loop1.getBody().size());
        assertEquals(1, loop2.getBody().size());

        removed.undo(commands);
        XMLCommandWriter.write(System.out, commands);
        assertEquals(5, commands.size());
        assertEquals(1, loop1.getBody().size());
        assertEquals(2, loop2.getBody().size());
        assertSame(command, loop2.getBody().get(1));
    }
}
