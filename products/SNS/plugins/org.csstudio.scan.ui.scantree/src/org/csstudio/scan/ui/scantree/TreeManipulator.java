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
public class TreeManipulator
{
    /** @param commands List of scan commands
     *  @param target Item before or after which new command should be inserted
     *  @param command New command to insert
     *  @param after <code>true</code> to insert after target, else before
     *  @return <code>true</code> if insertion succeeded
     */
    public static boolean remove(final List<ScanCommand> commands, final ScanCommand command)
    {
        for (int i=0; i<commands.size(); ++i)
        {
            final ScanCommand current = commands.get(i);
            if (current == command)
            {   // Found the item
                commands.remove(i);
                return true;
            }
            else if (current instanceof LoopCommand)
            {   // Recurse into loop, because target may be inside that loop.
                // Loop body may be read-only, so create writable copy...
                final LoopCommand loop = (LoopCommand) current;
                final List<ScanCommand> body = new ArrayList<ScanCommand>(loop.getBody());
                if (remove(body, command))
                {   // ... and update loop with that on success
                    loop.setBody(body);
                    return true;
                }
                // else: target wasn't in that loop
            }
        }
        return false;
    }

    /** @param commands List of scan commands
     *  @param target Item after which new command should be inserted
     *  @param command New command to insert
     *  @return <code>true</code> if insertion succeeded
     */
    public static boolean insertAfter(final List<ScanCommand> commands,
            final ScanCommand target, final ScanCommand command)
    {
        return insert(commands, target, command, true);
    }

    /** @param commands List of scan commands
     *  @param target Item before which new command should be inserted
     *  @param command New command to insert
     *  @return <code>true</code> if insertion succeeded
     */
    public static boolean insertBefore(final List<ScanCommand> commands,
            final ScanCommand target, final ScanCommand command)
    {
        return insert(commands, target, command, false);
    }

    /** @param commands List of scan commands
     *  @param target Item before or after which new command should be inserted
     *  @param command New command to insert
     *  @param after <code>true</code> to insert after target, else before
     *  @return <code>true</code> if insertion succeeded
     */
    public static boolean insert(final List<ScanCommand> commands,
            final ScanCommand target, final ScanCommand command, final boolean after)
    {
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
                // Loop body may be read-only, so create writable copy...
                final LoopCommand loop = (LoopCommand) current;
                final List<ScanCommand> body = new ArrayList<ScanCommand>(loop.getBody());
                if (insert(body, target, command, after))
                {   // ... and update loop with that on success
                    loop.setBody(body);
                    return true;
                }
                // else: target wasn't in that loop
            }
        }
        return false;
    }
}
