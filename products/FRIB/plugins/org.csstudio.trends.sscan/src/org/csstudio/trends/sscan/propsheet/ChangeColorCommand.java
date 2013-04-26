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
import org.eclipse.swt.graphics.RGB;

/** Undo-able command to change item's color
 *  @author Kay Kasemir
 */
public class ChangeColorCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private RGB old_color, new_color;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_color New value
     */
    public ChangeColorCommand(final OperationsManager operations_manager,
            final ModelItem item, final RGB new_color)
    {
        this.item = item;
        this.old_color = item.getColor();
        this.new_color = new_color;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setColor(new_color);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setColor(old_color);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Color;
    }
}
