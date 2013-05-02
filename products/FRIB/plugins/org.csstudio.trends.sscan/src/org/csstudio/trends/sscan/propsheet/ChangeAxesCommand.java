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
import org.csstudio.trends.sscan.model.AxesConfig;
import org.csstudio.trends.sscan.model.AxisConfig;
import org.csstudio.trends.sscan.model.ModelItem;

/** Undo-able command to change item's axis
 *  @author Kay Kasemir
 */
public class ChangeAxesCommand implements IUndoableCommand
{
    final private ModelItem item;
    final private AxesConfig old_axis, new_axis;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param axis New value
     */
    public ChangeAxesCommand(final OperationsManager operations_manager,
            final ModelItem item, final AxesConfig axes)
    {
        this.item = item;
        this.old_axis = item.getAxes();
        this.new_axis = axes;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        item.setAxes(new_axis);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setAxes(old_axis);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Axis;
    }
}
