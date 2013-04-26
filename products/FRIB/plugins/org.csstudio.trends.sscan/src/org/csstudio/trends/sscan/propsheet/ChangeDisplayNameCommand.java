/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.ModelItem;

/** Undo-able command to change item's display name
 *  @author Kay Kasemir
 */
public class ChangeDisplayNameCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private String old_name, new_name;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_name New value
     */
    public ChangeDisplayNameCommand(final OperationsManager operations_manager,
            final ModelItem item, final String new_name)
    {
        this.item = item;
        this.old_name = item.getDisplayName();
        this.new_name = new_name;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setDisplayName(new_name);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setDisplayName(old_name);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.TraceDisplayName;
    }
}
