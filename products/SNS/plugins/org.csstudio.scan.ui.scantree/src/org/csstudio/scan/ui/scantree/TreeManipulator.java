/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;

/** Helper for manipulating the 'tree' of scan commands.
 *
 *  <p>If the scan tree was an actual tree, this
 *  might be simpler, but for now we keep the original
 *  {@link List} API and thus the tree-based operations
 *  are handled in here.
 *
 *  <p>Note that all comparisons in here are via '==',
 *  not <code>equals()</code>.
 *  When specifying insertion targets one must provide
 *  the exact tree item for the insertion, not an element
 *  that might be "equal" in value but outside of the tree.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TreeManipulator
{
    /** Info about a removed item,
     *  allowing re-insertion at the original place
     */
    public static class RemovalInfo
    {
        final private ScanCommand command;
        final private ScanCommand parent;
        final private ScanCommand previous;

        /** @param parent Parent item, for example Loop or <code>null</code> for top-level item
         *  @param previous Previous item within the parent or top-level list, <code>null</code> if first
         *  @param command Command that was removed
         */
        public RemovalInfo(final ScanCommand parent, final ScanCommand previous,
                final ScanCommand command)
        {
            this.command = command;
            this.parent = parent;
            this.previous = previous;
        }

        /** Undo the removal
         *  @param commands List where removal took place
         *  @throws Exception on error
         */
        public void undo(final List<ScanCommand> commands) throws Exception
        {
            if (! reinsert(null, commands))
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
                insert(commands, previous, command, true);
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

    /** @param commands List of scan commands
     *  @param command Command to remove
     *  @return Info about removal
     *  @throws Exception on error
     */
    public static RemovalInfo remove(final List<ScanCommand> commands,
            final ScanCommand command) throws Exception
    {
        final RemovalInfo info = remove(null, commands, command);
        if (info == null)
            throw new Exception("Cannot locate item to be removed");
        return info;
    }

    /** @param parent Parent item, <code>null</code> for root of tree
     *  @param commands List of scan commands under parent
     *  @param command Command to remove
     *  @return Info about removal
     */
    private static RemovalInfo remove(final ScanCommand parent,
            final List<ScanCommand> commands, final ScanCommand command)
    {
        for (int i=0; i<commands.size(); ++i)
        {
            final ScanCommand current = commands.get(i);
            if (current == command)
            {   // Found the item
                commands.remove(i);
                return new RemovalInfo(parent, i > 0 ? commands.get(i-1) : null, command);
            }
            else if (current instanceof LoopCommand)
            {   // Recurse into loop, because target may be inside that loop.
                // Loop body may be read-only, so create writable copy...
                final LoopCommand loop = (LoopCommand) current;
                final List<ScanCommand> body = new ArrayList<ScanCommand>(loop.getBody());
                final RemovalInfo info = remove(loop, body, command);
                if (info != null)
                {   // ... and update loop with that on success
                    loop.setBody(body);
                    return info;
                }
                // else: target wasn't in that loop
            }
        }
        return null;
    }

    /** @param commands List of scan commands
     *  @param target Item before or after which new command should be inserted.
     *                If <code>null</code>, inserts at start of list.
     *  @param command New command to insert
     *  @param after <code>true</code> to insert after target, else before
     *  @throws Exception if element cannot be inserted
     */
    public static void insert(final List<ScanCommand> commands,
            final ScanCommand target, final ScanCommand command, final boolean after) throws Exception
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
    private static boolean doInsert(final List<ScanCommand> commands,
            final ScanCommand target, final ScanCommand command, final boolean after)
    {
        if (target == null)
        {
            commands.add(0, command);
            return true;
        }
        for (int i=0; i<commands.size(); ++i)
        {
            final ScanCommand current = commands.get(i);
            if (current == target)
            {   // Found the insertion point
                commands.add(after ? i+1 : i, command);
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
    public static void addToLoop(final LoopCommand loop, final ScanCommand command)
    {
        loop.getBody().add(0, command);
    }
}
