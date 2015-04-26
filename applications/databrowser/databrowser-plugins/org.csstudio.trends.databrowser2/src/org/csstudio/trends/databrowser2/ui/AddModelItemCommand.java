/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.util.Optional;

import org.csstudio.swt.rtplot.undo.UndoableAction;
import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.FormulaInput;
import org.csstudio.trends.databrowser2.model.FormulaItem;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

/** Undo-able command to add a ModelItem to the Model
 *  @author Kay Kasemir
 */
public class AddModelItemCommand extends UndoableAction
{
    final private Shell shell;
    final private Model model;
    final private ModelItem item;

    /** Create PV via undo-able AddModelItemCommand,
     *  displaying errors in dialog
     *  @param shell Shell used for error dialogs
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param model Model were PV is to be added
     *  @param pv_name Name of new PV
     *  @param period scan period
     *  @param axis Axis
     *  @param archive Archive data source
     *  @return AddModelItemCommand or <code>null</code> on error
     */
    public static Optional<AddModelItemCommand> forPV(final Shell shell,
            final UndoableActionManager operations_manager,
            final Model model,
            final String pv_name,
            final double period,
            final AxisConfig axis,
            final ArchiveDataSource archive)
    {
        // Create item
        final PVItem item;
        try
        {
            item = new PVItem(pv_name, period);
            if (archive != null)
                item.addArchiveDataSource(archive);
            else
                item.useDefaultArchiveDataSources();
            axis.setVisible(true);
            item.setAxis(axis);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell,
                    Messages.Error,
                    NLS.bind(Messages.AddItemErrorFmt, pv_name, ex.getMessage()));
            return Optional.empty();
        }
        // Add to model via undo-able command
        return Optional.of(new AddModelItemCommand(shell, operations_manager, model, item));
    }

    /** Create PV via undo-able AddModelItemCommand,
     *  displaying errors in dialog
     *  @param shell Shell used for error dialogs
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param model Model were PV is to be added
     *  @param axis Axis
     *  @return AddModelItemCommand or <code>null</code> on error
     */
    public static Optional<AddModelItemCommand> forFormula(final Shell shell,
            final UndoableActionManager operations_manager,
            final Model model,
            final String formula_name,
            final AxisConfig axis)
    {
        // Create item
        final FormulaItem item;
        try
        {
            item = new FormulaItem(formula_name, "0", new FormulaInput[0]); //$NON-NLS-1$
            axis.setVisible(true);
            item.setAxis(axis);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell,
                    Messages.Error,
                    NLS.bind(Messages.AddItemErrorFmt, formula_name, ex.getMessage()));
            return Optional.empty();
        }
        // Add to model via undo-able command
        return Optional.of(new AddModelItemCommand(shell, operations_manager, model, item));
    }


    /** Register and perform the command
     *  @param shell Shell used for error dialogs
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param model Model were PV is to be added
     *  @param item Item to add
     */
    public AddModelItemCommand(final Shell shell,
            final UndoableActionManager operations_manager,
            final Model model,
            final ModelItem item)
    {
        super(Messages.AddPV);
        this.shell = shell;
        this.model = model;
        this.item = item;
        try
        {
            model.addItem(item);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell,
                    Messages.Error,
                    NLS.bind(Messages.AddItemErrorFmt, item.getName(), ex.getMessage()));
            // Exit before registering for undo because there's nothing to undo
            return;
        }
        operations_manager.add(this);
    }

    /** @return {@link ModelItem} (PV, Formula) that this command added */
    public ModelItem getItem()
    {
        return item;
    }

    /** {@inheritDoc} */
    @Override
    public void run()
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

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        model.removeItem(item);
    }
}
