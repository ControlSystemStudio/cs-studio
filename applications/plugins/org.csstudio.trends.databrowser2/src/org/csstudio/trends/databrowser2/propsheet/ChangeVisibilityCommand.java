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

/** Undo-able command to change a PV item's request type
 *  @author Kay Kasemir
 */
public class ChangeVisibilityCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private boolean old_visibility, new_visibility;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_trace_type New value
     */
    public ChangeVisibilityCommand(final OperationsManager operations_manager,
            final ModelItem item, final boolean visible)
    {
        this.item = item;
        this.old_visibility = item.isVisible();
        this.new_visibility = visible;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
		AxisConfig axis = item.getAxis();
		// check if this is the last/only visible item on the axis
		if (item.getModel().countActiveItemsOnAxis(axis) < 2) {
			axis.setVisible(new_visibility);
		}
		item.setVisible(new_visibility);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
		AxisConfig axis = item.getAxis();
		// check if this is the last/only visible item on the axis
		if (item.getModel().countActiveItemsOnAxis(axis) < 2) {
			axis.setVisible(old_visibility);
		}
		item.setVisible(old_visibility);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.TraceVisibility;
    }
}
