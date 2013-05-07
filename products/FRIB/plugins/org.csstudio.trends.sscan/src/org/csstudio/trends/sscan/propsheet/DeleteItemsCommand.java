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
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Undo-able command to delete items
 *  @author Kay Kasemir
 */
public class DeleteItemsCommand implements IUndoableCommand
{
    final private Shell shell;
    final private Model model;
    final private ModelItem items[];

    /** Register and perform the command
     *  @param shell Shell used for error dialogs
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param model Model were PV is to be added
     *  @param items Model items to delete
     */
    public DeleteItemsCommand(final Shell shell,
            final OperationsManager operations_manager,
            final Model model,
            final ModelItem items[])
    {
        this.shell = shell;
        this.model = model;
        this.items = items;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        for (ModelItem item : items)
            model.removeItem(item);
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        for (ModelItem item : items)
        {
            try
            {
                model.addItem(item);
            }
            catch (Exception ex)
            {
                MessageDialog.openError(shell,
                        Messages.Error,
                        NLS.bind(Messages.AddItemErrorFmt, item.getName(), ex.getMessage()));
            }
        }
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.DeleteItem;
    }
}
