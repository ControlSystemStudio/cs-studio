/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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

import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.csstudio.scan.ui.scantree.model.RemovalInfo;
import org.csstudio.scan.ui.scantree.model.ScanTreeModel;
import org.csstudio.scan.ui.scantree.model.ScanTreeModelListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the {@link ScanTreeModel}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanTreeModelTest implements ScanTreeModelListener
{
    final private ScanTreeModel model = new ScanTreeModel();
    private int changes = 0;
    private int additions = 0;
    private int removals = 0;
    private ScanCommand update_command = null;

    @Override
    public void commandsChanged()
    {
        ++changes;
    }

    @Override
    public void commandAdded(final ScanCommand command)
    {
        ++additions;
        update_command = command;
    }

    @Override
    public void commandRemoved(final ScanCommand command)
    {
        ++removals;
        update_command = command;
    }

    @Override
    public void commandPropertyChanged(ScanCommand command)
    {
        ++changes;
        update_command = command;
    }

    @Before
    public void before()
    {
        model.addListener(this);
        model.setCommands(DemoScan.createCommands());
    }

    @After
    public void after()
    {
        model.removeListener(this);
    }

    @Test
    public void testSetCommands()
    {
        assertEquals(1, changes);
    }

    @Test
    public void testGetChildren()
    {
        final List<ScanCommand> commands = model.getCommands();
        assertNull(model.getChildren(commands.get(0)));
        assertNotNull(model.getChildren(commands.get(4)));
    }

    @Test
    public void testGetChildCount() throws Exception
    {
        final List<ScanCommand> commands = model.getCommands();
        XMLCommandWriter.write(System.out, commands);
        assertEquals(0, model.getChildCount(commands.get(0)));
        assertEquals(1, model.getChildCount(commands.get(4)));
        assertEquals(2, model.getChildCount(model.getChildren(commands.get(4)).get(0)));
    }

    @Test
    public void testGetParent()
    {
        final List<ScanCommand> commands = model.getCommands();
        ScanCommand loop = commands.get(4);
        assertNull(model.getParent(loop));
        final ScanCommand child = model.getChildren(loop).get(0);
        assertSame(loop, model.getParent(child));
    }

    @Test
    public void testInsert() throws Exception
    {
        final List<ScanCommand> commands = model.getCommands();
        assertEquals(5, commands.size());
        ScanCommand command = new LogCommand("add1");
        model.insert(null, command, true);
        assertEquals(6, commands.size());
        assertSame(command, commands.get(0));
        assertEquals(1, changes);
        assertEquals(1, additions);
        assertSame(command, update_command);

        command = new LogCommand("add2");
        model.insert(commands.get(5), command, false);
        assertEquals(7, commands.size());
        assertSame(command, commands.get(5));
        assertEquals(1, changes);
        assertEquals(2, additions);
        assertSame(command, update_command);

        XMLCommandWriter.write(System.out, commands);
        command = new LogCommand("add3");
        model.addToLoop((LoopCommand) commands.get(6), command);
        assertEquals(7, commands.size());
        assertEquals(3, additions);
        assertSame(command, update_command);
        assertEquals(command, model.getChildren(commands.get(6)).get(0));
    }

    @Test
    public void testRemove() throws Exception
    {
        final List<ScanCommand> commands = model.getCommands();
        XMLCommandWriter.write(System.out, commands);

        final ScanCommand command = commands.get(4);
        RemovalInfo removal = model.remove(command);
        assertEquals(4, commands.size());
        assertEquals(1, removals);
        assertSame(command, update_command);

        update_command = null;
        removal.undo();
        assertEquals(5, commands.size());
        assertEquals(1, additions);
        assertSame(command, update_command);
    }

    @Test
    public void testLoopRemoval() throws Exception
    {
        final List<ScanCommand> commands = model.getCommands();
        final LoopCommand loop = (LoopCommand) model.getChildren(commands.get(4)).get(0);
        XMLCommandWriter.write(System.out, Arrays.asList((ScanCommand)loop));
        assertEquals(2, loop.getBody().size());

        ScanCommand command = loop.getBody().get(0);
        RemovalInfo removal = model.remove(command);
        assertEquals(1, loop.getBody().size());

        removal.undo();
        assertEquals(2, loop.getBody().size());
    }

    @Test
    public void testPropertyChange() throws Exception
    {
        final List<ScanCommand> commands = model.getCommands();
        final ScanCommand command = commands.get(0);
        assertSame(SetCommand.class, command.getClass());

        model.changeProperty(command, "device_name", "fred");
        assertEquals(2, changes);
        assertSame(command, update_command);
    }
}
