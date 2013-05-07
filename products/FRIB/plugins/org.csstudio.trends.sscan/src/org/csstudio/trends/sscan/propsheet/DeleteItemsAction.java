/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.FormulaItem;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.ModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Context menu action that deletes items that are currently selected
 *  in the trace table from the Model
 *  @author Kay Kasemir
 */
public class DeleteItemsAction extends Action
{
    final private OperationsManager operations_manager;
    final private TableViewer trace_table;
    final private Model model;

    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param trace_table Table of ModelItems
     *  @param model Model were PVs will be deleted
     */
    public DeleteItemsAction(final OperationsManager operations_manager,
            final TableViewer trace_table, final Model model)
    {
        super(Messages.DeleteItem,
              PlatformUI.getWorkbench().getSharedImages()
                      .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        this.operations_manager = operations_manager;
        this.trace_table = trace_table;
        this.model = model;
        // Only enabled when something's selected
        final ISelectionChangedListener selection_listener = new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(final SelectionChangedEvent event)
            {
                setEnabled(!trace_table.getSelection().isEmpty());
            }
        };
        trace_table.addSelectionChangedListener(selection_listener);
        selection_listener.selectionChanged(null);
    }

    @Override
    public void run()
    {
        // Get selected objects from table, turn into ModelItem array
        final Object[] objects =
            ((IStructuredSelection)trace_table.getSelection()).toArray();
        final ModelItem items[] = new ModelItem[objects.length];
        for (int i = 0; i < items.length; i++)
        {
            items[i] = (ModelItem) objects[i];
            // Check if item is used as input for formula
            final FormulaItem formula = model.getFormulaWithInput(items[i]);
            if (formula != null)
            {
                MessageDialog.openError(trace_table.getTable().getShell(),
                        Messages.Error,
                        NLS.bind(Messages.PVUsedInFormulaFmt, items[i].getName(), formula.getName()));
                return;
            }
        }
        // Delete via undo-able command
        new DeleteItemsCommand(trace_table.getTable().getShell(),
                operations_manager, model, items);
    }
}
