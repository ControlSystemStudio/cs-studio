/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.rtplot.undo.UndoableAction;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;

/** Undo-able command to change plot update period
 *  @author Kay Kasemir
 */
public class ChangeUpdatePeriodCommand extends UndoableAction
{
    final private Model model;
    final private double old_period, new_period;

    /** Register and perform the command
     *  @param item Model item to configure
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param period New value
     */
    public ChangeUpdatePeriodCommand(final Model model,
            final UndoableActionManager operations_manager,
            final double period)
    {
        super(Messages.UpdatePeriodLbl);
        this.model = model;
        this.old_period = model.getUpdatePeriod();
        this.new_period = period;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.setUpdatePeriod(new_period);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.setUpdatePeriod(old_period);
    }
}
