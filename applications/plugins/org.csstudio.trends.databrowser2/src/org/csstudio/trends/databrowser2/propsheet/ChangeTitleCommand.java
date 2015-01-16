/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
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

/** Undo-able command to change plot title
 *  @author Kay Kasemir
 */
public class ChangeTitleCommand extends UndoableAction
{
    final private Model model;
    final private String old_title, new_title;

    /** Register and perform the command
     *  @param item Model item to configure
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param period New value
     */
    public ChangeTitleCommand(final Model model,
            final UndoableActionManager operations_manager,
            final String title)
    {
        super(Messages.TitleLbl);
        this.model = model;
        this.old_title = model.getTitle().orElse(null);
        this.new_title = title;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.setTitle(new_title);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.setTitle(old_title);
    }
}
