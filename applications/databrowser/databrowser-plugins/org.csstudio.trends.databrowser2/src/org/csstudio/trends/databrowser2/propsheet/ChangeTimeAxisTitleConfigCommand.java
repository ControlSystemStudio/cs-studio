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

/** Undo-able command to change time axis configuration
 *  @author Kay Kasemir
 */
public class ChangeTimeAxisTitleConfigCommand extends UndoableAction
{
    final private Model model;
    final private boolean show_label;

    /** Register the command and perform
     *  @param model
     *  @param operations_manager
     *  @param show_label
     */
    public ChangeTimeAxisTitleConfigCommand(final Model model,
            final UndoableActionManager operations_manager,
            final boolean show_label)
    {
        super(Messages.TimeAxis);
        this.model = model;
        this.show_label = show_label;
        operations_manager.add(this);
        run();
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.setTimeAxisTitleVisible(show_label);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.setTimeAxisTitleVisible(!show_label);
    }
}
