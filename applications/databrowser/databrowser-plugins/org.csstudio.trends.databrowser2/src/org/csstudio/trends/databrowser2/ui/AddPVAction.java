/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.csstudio.swt.rtplot.undo.UndoableActionManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.FormulaItem;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.propsheet.AddAxisCommand;
import org.csstudio.trends.databrowser2.propsheet.EditFormulaDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/** Context menu action that adds a PV or Formula to the Model
 *  @author Kay Kasemir
 */
public class AddPVAction extends Action
{
    final private UndoableActionManager operations_manager;
    final private Shell shell;
    final private Model model;
    final private boolean formula;

    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param trace_table Table of ModelItems, used to get Shell
     *  @param model Model were PVs will be added
     */
    @SuppressWarnings("nls")
    public AddPVAction(final UndoableActionManager operations_manager,
            final Shell shell, final Model model, final boolean formula)
    {
        super(formula ? Messages.AddFormula : Messages.AddPV,
              Activator.getDefault().getImageDescriptor(
                      formula ? "icons/add_formula.gif" : "icons/add.gif"));
        this.operations_manager = operations_manager;
        this.shell = shell;
        this.model = model;
        this.formula = formula;
    }

    @Override
    public void run()
    {
        runWithSuggestedName(null, null);
    }

    /** Run the 'add PV' dialog with optional defaults
     *  @param name Suggested PV name, for example from drag-n-drop
     *  @param archive Archive data source for the new PV
     *  @return <code>true</code> if PV name was added, <code>false</code> if canceled by user
     */
    public boolean runWithSuggestedName(final String name, final ArchiveDataSource archive)
    {
        // Prompt for PV name
        final Set<String> existing_names = new HashSet<>();
        for (ModelItem item : model.getItems())
            existing_names.add(item.getName());
        final List<AxisConfig> axes = new ArrayList<>();
        final List<String> axes_names = new ArrayList<>();
        for (AxisConfig axis : model.getAxes())
        {
            axes.add(axis);
            axes_names.add(axis.getName());
        }
        final AddPVDialog dlg = new AddPVDialog(shell, existing_names, axes_names, formula);
        dlg.setName(name);
        if (dlg.open() != Window.OK)
            return false;

        // Did user select axis?
        final AxisConfig axis;
        if (dlg.getAxisIndex() >= 0)
            axis = axes.get(dlg.getAxisIndex());
        else // Use first empty axis, or create a new one
            axis = model.getEmptyAxis().orElseGet(() -> new AddAxisCommand(operations_manager, model).getAxis());

        // Create item
        if (formula)
        {
            final Optional<AddModelItemCommand> command = AddModelItemCommand.forFormula(
                        shell, operations_manager, model, dlg.getName(), axis);
            if (! command.isPresent())
                return false;
            // Open configuration dialog
            final FormulaItem formula = (FormulaItem) command.get().getItem();
            final EditFormulaDialog edit =
                new EditFormulaDialog(operations_manager, shell, formula);
            edit.open();
        }
        else
            AddModelItemCommand.forPV(shell, operations_manager, model,
                dlg.getName(), dlg.getScanPeriod(), axis, archive);
        return true;
    }
}
