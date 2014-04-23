/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.model;

import java.util.List;

import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;

/** Info about a removed item,
 *  allowing re-insertion at the original place
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RemovalInfo
{
    final private ScanTreeModel model;
    final private ScanCommand command;
    final private ScanCommand parent;
    final private ScanCommand previous;

    /** @param model Scan tree model
     *  @param parent Parent item, for example Loop or <code>null</code> for top-level item
     *  @param previous Previous item within the parent or top-level list, <code>null</code> if first
     *  @param command Command that was removed
     */
    public RemovalInfo(final ScanTreeModel model,
            final ScanCommand parent, final ScanCommand previous,
            final ScanCommand command)
    {
        this.model = model;
        this.command = command;
        this.parent = parent;
        this.previous = previous;
    }

    /** @return Command that was removed */
    public ScanCommand getCommand()
    {
        return command;
    }

    /** Undo the removal
     *  @throws Exception on error
     */
    public void undo() throws Exception
    {
        if (! reinsert(null, model.getCommands()))
            throw new Exception("Cannot re-insert cut command");
    }

    /** Recursively attempt to insert removed item
     *  @param commands_parent Parent of commands
     *  @param commands List of commands
     *  @return <code>true</code> if successful
     *  @throws Exception on error
     */
    private boolean reinsert(final ScanCommand commands_parent, final List<ScanCommand> commands) throws Exception
    {
        // Was command removed at this level in the tree?
        if (commands_parent == parent)
        {
            model.insert(commands, previous, command, true);
            return true;
        }

        // Descend down the tree
        for (ScanCommand item : commands)
            if (item instanceof LoopCommand)
            {   // Can command be re-inserted at or below this loop?
                final LoopCommand loop = (LoopCommand) item;
                if (reinsert(loop, loop.getBody()))
                    return true;
                // else: keep looking
            }
        return false;
    }
}
