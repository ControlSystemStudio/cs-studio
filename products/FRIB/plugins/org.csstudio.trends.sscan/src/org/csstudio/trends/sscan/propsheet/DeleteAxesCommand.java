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
import org.csstudio.trends.sscan.model.Model;

/** Undo-able command to delete value axis from Model.
 *  @author Kay Kasemir
 */
public class DeleteAxesCommand implements IUndoableCommand
{
    /** Model to which axis is added */
    final private Model model;

    /** The axis that was removed */
    final private AxesConfig axes;

    /** Index where axis used to be */
    final private int index;

    /** Initialize
     *  @param operationsManager
     *  @param model
     *  @param axis
     */
    public DeleteAxesCommand(final OperationsManager operationsManager,
            final Model model, final AxesConfig axes)
    {
        this.model = model;
        this.axes = axes;
        // Remember axis locations
        this.index = model.getAxesIndex(axes);
        operationsManager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        model.removeAxes(axes);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.addAxes(index, axes);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.DeleteAxis;
    }
}
