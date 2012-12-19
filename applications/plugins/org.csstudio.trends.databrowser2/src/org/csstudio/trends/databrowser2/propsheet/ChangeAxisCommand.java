/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.ModelItem;

/** Undo-able command to change item's axis
 *  @author Kay Kasemir
 */
public class ChangeAxisCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private AxisConfig old_axis, new_axis;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param axis New value
     */
    public ChangeAxisCommand(final OperationsManager operations_manager,
            final ModelItem item, final AxisConfig axis)
    {
        this.item = item;
        this.old_axis = item.getAxis();
        this.new_axis = axis;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
		if (!new_axis.isVisible())
			new_axis.setVisible(true);
		item.setAxis(new_axis);
		if (item.getModel().countActiveItemsOnAxis(old_axis) == 0)
			old_axis.setVisible(false);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
		if (!old_axis.isVisible())
			old_axis.setVisible(true);
		item.setAxis(old_axis);
		if (item.getModel().countActiveItemsOnAxis(new_axis) == 0)
			new_axis.setVisible(false);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
