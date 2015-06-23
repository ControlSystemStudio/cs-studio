/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.jface.action.Action;

/** Context menu action that moves item 'up' or 'down' within the model's traces.
 *  @author Kay Kasemir
 */
public class MoveItemAction extends Action
{
    final private UndoableActionManager operations_manager;
    final private Model model;
    final private ModelItem item;
    final private boolean up;

    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param model Model were PV will be moved
     *  @param item Item to move
     *  @param up True for 'up'
     */
    public MoveItemAction(final UndoableActionManager operations_manager,
            final Model model, final ModelItem item, final boolean up)
    {
        super(up ? Messages.MoveItemUp : Messages.MoveItemDown,
              Activator.getDefault().getImageDescriptor(up ? "icons/up.gif" : "icons/down.gif"));
        this.operations_manager = operations_manager;
        this.model = model;
        this.item = item;
        this.up = up;
    }

    @Override
    public void run()
    {
        new MoveItemCommand(operations_manager, model, item, up);
    }
}
