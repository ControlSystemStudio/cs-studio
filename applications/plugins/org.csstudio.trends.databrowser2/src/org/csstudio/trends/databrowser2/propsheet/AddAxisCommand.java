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

/** Undo-able command to add value axis to Msodel
 *  @author Kay Kasemir
 */
public class AddAxisCommand implements IUndoableCommand
{
    /** Model to which axis is added */
    final private Model model;

    /** The axis that was added */
    final private AxisConfig axis;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pv PV where to add archive
     *  @param archive Archive data source to add
     */
    public AddAxisCommand(final OperationsManager operations_manager,
            final Model model)
    {
        this.model = model;
        operations_manager.addCommand(this);
		axis = model.addAxis((String) null);
    }

    /** @return AxisConfig that was added */
    public AxisConfig getAxis()
    {
        return axis;
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        model.addAxis(axis);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.removeAxis(axis);
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.AddAxis;
    }
}
