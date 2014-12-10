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
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.swt.graphics.RGB;

/** Undo-able command to change item's color
 *  @author Kay Kasemir
 */
public class ChangeColorCommand extends UndoableAction
{
    final private ModelItem item;
    final private RGB old_color, new_color;

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param item Model item to configure
     *  @param new_color New value
     */
    public ChangeColorCommand(final UndoableActionManager operations_manager,
            final ModelItem item, final RGB new_color)
    {
        super(Messages.Color);
        this.item = item;
        this.old_color = item.getColor();
        this.new_color = new_color;
        operations_manager.execute(this);
    }

    /** {@inheritDoc} */
    @Override
    public void run()
    {
        item.setColor(new_color);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        item.setColor(old_color);
    }
}
