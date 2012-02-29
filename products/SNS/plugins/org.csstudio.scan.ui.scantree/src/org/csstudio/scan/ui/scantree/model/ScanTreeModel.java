/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;

/** Model of a scan tree
 *
 *  <p>Presents scan commands in a tree-type API
 *  and supports listener for GUI that needs to track changes
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanTreeModel
{
    final private List<ScanCommand> model = new ArrayList<ScanCommand>();

    final private List<ScanTreeModelListener> listeners = new CopyOnWriteArrayList<ScanTreeModelListener>();

    /** @param listener Listener to add */
    public void addListener(final ScanTreeModelListener listener)
    {
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final ScanTreeModelListener listener)
    {
        listeners.remove(listener);
    }

    /** @param commands Commands to use, replacing existing commands in model */
    public void setCommands(final List<ScanCommand> commands)
    {
        // Copy commands into existing list
        model.clear();
        model.addAll(commands);
        for (ScanTreeModelListener listener : listeners)
            listener.commandsChanged();
    }

    /** @return Scan commands */
    public List<ScanCommand> getCommands()
    {
        return model;
    }

    /** Determine child count
     *  @param command command for which to determine the child count
     *  @return child count, may be 0
     */
    public int getChildCount(final ScanCommand command)
    {
        final List<ScanCommand> children = getChildren(command);
        if (children != null)
            return children.size();
        return 0;
    }

    /** Determine child elements in tree
     *  @param command command for which to determine the child commands
     *  @return child commands or <code>null</code>
     */
    public List<ScanCommand> getChildren(final ScanCommand command)
    {
        if (command instanceof LoopCommand)
        {
            final LoopCommand loop = (LoopCommand) command;
            return loop.getBody();
        }
        return null;
    }

    /** @param command Command for which to determine the parent command
     *  @return Parent command or <code>null</code>
     */
    public ScanCommand getParent(final ScanCommand command)
    {
        return checkChildren(null, model, command);
    }

    /** Recursively determine parent item
     *  @param parent Possible parent
     *  @param children child entries of parent
     *  @param desired_child Desired child element
     *  @return Parent element of child or <code>null</code>
     */
    private ScanCommand checkChildren(final ScanCommand parent, final List<ScanCommand> children, final ScanCommand desired_child)
    {
        for (ScanCommand child : children)
        {
            if (child == desired_child)
                return parent;
            if (getChildCount(child) > 0)
            {
                final ScanCommand found = checkChildren(child, getChildren(child), desired_child);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    /** @param target Item before or after which new command should be inserted.
     *                If <code>null</code>, inserts at start of list.
     *  @param command New command to insert
     *  @param after <code>true</code> to insert after target, else before
     *  @throws Exception if element cannot be inserted
     */
    public void insert(final ScanCommand target, final ScanCommand command, final boolean after) throws Exception
    {
        insert(model, target, command, after);
    }


    /** @param commands Commands, either 'root' of model or body of a loop
     *  @param target Item before or after which new command should be inserted.
     *                If <code>null</code>, inserts at start of list.
     *  @param command New command to insert
     *  @param after <code>true</code> to insert after target, else before
     *  @throws Exception if element cannot be inserted
     */
    public void insert(final List<ScanCommand> commands, final ScanCommand target, final ScanCommand command, final boolean after) throws Exception
    {
        if (! doInsert(commands, target, command, after))
            throw new Exception("Cannot locate insertion point for command in list");
    }


    /** Insert command in list, recursing down to find insertion target
     *  @param commands List of scan commands
     *  @param target Item before or after which new command should be inserted.
     *                If <code>null</code>, inserts at start of list.
     *  @param command New command to insert
     *  @param after <code>true</code> to insert after target, else before
     *  @return <code>true</code> if command could be inserted in this list
     */
    private boolean doInsert(final List<ScanCommand> commands,
            final ScanCommand target, final ScanCommand command, final boolean after)
    {
        if (target == null)
        {
            commands.add(0, command);
            for (ScanTreeModelListener listener : listeners)
                listener.commandAdded(command);
            return true;
        }
        for (int i=0; i<commands.size(); ++i)
        {
            final ScanCommand current = commands.get(i);
            if (current == target)
            {   // Found the insertion point
                commands.add(after ? i+1 : i, command);
                for (ScanTreeModelListener listener : listeners)
                    listener.commandAdded(command);
                return true;
            }
            else if (current instanceof LoopCommand)
            {   // Recurse into loop, because target may be inside that loop.
                final LoopCommand loop = (LoopCommand) current;
                if (doInsert(loop.getBody(), target, command, after))
                    return true;
                // else: target wasn't in that loop
            }
        }
        return false;
    }

    /** @param loop Loop to which to add a command
     *  @param command New command to insert
     */
    public void addToLoop(final LoopCommand loop, final ScanCommand command)
    {
        loop.getBody().add(0, command);
        for (ScanTreeModelListener listener : listeners)
            listener.commandAdded(command);
    }

    /** @param command Command to remove
     *  @return Info about removal
     *  @throws Exception on error
     */
    public RemovalInfo remove(final ScanCommand command) throws Exception
    {
        final RemovalInfo info = remove(null, model, command);
        if (info == null)
            throw new Exception("Cannot locate item to be removed");
        return info;
    }

    /** @param parent Parent item, <code>null</code> for root of tree
     *  @param commands List of scan commands under parent
     *  @param command Command to remove
     *  @return Info about removal
     */
    private RemovalInfo remove(final ScanCommand parent,
            final List<ScanCommand> commands, final ScanCommand command)
    {
        for (int i=0; i<commands.size(); ++i)
        {
            final ScanCommand current = commands.get(i);
            if (current == command)
            {   // Found the item
                commands.remove(i);
                for (ScanTreeModelListener listener : listeners)
                    listener.commandRemoved(current);
                return new RemovalInfo(this, parent, i > 0 ? commands.get(i-1) : null, command);
            }
            else if (current instanceof LoopCommand)
            {   // Recurse into loop, because target may be inside that loop.
                final LoopCommand loop = (LoopCommand) current;
                final List<ScanCommand> body = loop.getBody();
                final RemovalInfo info = remove(loop, body, command);
                if (info != null)
                    return info;
                // else: target wasn't in that loop
            }
        }
        return null;
    }

    /** @param command Command where property should be changed
     *  @param property Property to change
     *  @param new_value New value for property
     *  @throws Exception on error, for example unknown property
     */
    public void changeProperty(final ScanCommand command,
            final String property, final Object new_value) throws Exception
    {
        command.setProperty(property, new_value);
        for (ScanTreeModelListener listener : listeners)
            listener.commandPropertyChanged(command);
    }
}
