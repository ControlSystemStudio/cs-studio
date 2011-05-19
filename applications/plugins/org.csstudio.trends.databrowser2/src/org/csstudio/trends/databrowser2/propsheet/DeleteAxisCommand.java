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
import org.csstudio.trends.databrowser2.model.Model;

/** Undo-able command to delete value axis from Model.
 *  @author Kay Kasemir
 */
public class DeleteAxisCommand implements IUndoableCommand
{
    /** Model to which axis is added */
    final private Model model;

    /** The axis that was removed */
    final private AxisConfig axis;

    /** Index where axis used to be */
    final private int index;

    /** Initialize
     *  @param operationsManager
     *  @param model
     *  @param axis
     */
    public DeleteAxisCommand(final OperationsManager operationsManager,
            final Model model, final AxisConfig axis)
    {
        this.model = model;
        this.axis = axis;
        // Remember axis locations
        this.index = model.getAxisIndex(axis);
        operationsManager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        model.removeAxis(axis);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.addAxis(index, axis);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.DeleteAxis;
    }
}
