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

/** Undo-able command to change save-on-change behavior
 *  @author Kay Kasemir
 */
public class ChangeSaveChangesCommand extends UndoableAction
{
    final private Model model;
    final private boolean save_changes;

    /** Register and perform the command
     *  @param model Model
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param new_trace_type New value
     */
    public ChangeSaveChangesCommand(final Model model,
            final UndoableActionManager operations_manager,
            final boolean save_changes)
    {
        super(Messages.SaveChangesLbl);
        this.model = model;
        this.save_changes = save_changes;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.setSaveChanges(save_changes);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.setSaveChanges(! save_changes);
    }
}
