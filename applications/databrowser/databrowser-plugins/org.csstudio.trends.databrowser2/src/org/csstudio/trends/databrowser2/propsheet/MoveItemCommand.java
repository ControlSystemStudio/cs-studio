/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
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
import org.csstudio.trends.databrowser2.model.ModelItem;

/** Undo-able command to move an item
 *  @author Kay Kasemir
 */
public class MoveItemCommand extends UndoableAction
{
    final private Model model;
    final private ModelItem item;
    final private boolean up;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param model Model
     *  @param item Model item to move
     *  @param up True for 'up'
     */
    public MoveItemCommand(final UndoableActionManager operations_manager,
            final Model model, final ModelItem item, final boolean up)
    {
        super(Messages.DeleteItem);
        this.model = model;
        this.item = item;
        this.up = up;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        model.moveItem(item, up);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.moveItem(item, !up);
    }
}
