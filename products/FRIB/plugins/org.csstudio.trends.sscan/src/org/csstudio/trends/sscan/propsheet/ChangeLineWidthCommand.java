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

/** Undo-able command to change item's line width
 *  @author Kay Kasemir
 */
public class ChangeLineWidthCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private int old_width, new_width;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_width New value
     */
    public ChangeLineWidthCommand(final OperationsManager operations_manager,
            final ModelItem item, final int new_width)
    {
        this.item = item;
        this.old_width = item.getLineWidth();
        this.new_width = new_width;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setLineWidth(new_width);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setLineWidth(old_width);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.TraceLineWidth;
    }
}
