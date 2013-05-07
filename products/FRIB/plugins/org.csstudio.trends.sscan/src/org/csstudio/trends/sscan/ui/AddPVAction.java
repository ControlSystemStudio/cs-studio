/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.model.AxesConfig;
import org.csstudio.trends.sscan.model.AxisConfig;
import org.csstudio.trends.sscan.model.FormulaItem;
import org.csstudio.trends.sscan.model.Model;
import org.csstudio.trends.sscan.model.Sscan;
import org.csstudio.trends.sscan.propsheet.AddAxesCommand;
import org.csstudio.trends.sscan.propsheet.EditFormulaDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/** Context menu action that adds a PV or Formula to the Model
 *  @author Kay Kasemir
 */
public class AddPVAction extends Action
{
    final private OperationsManager operations_manager;
    final private Shell shell;
    final private Model model;
    final private boolean formula;

    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param trace_table Table of ModelItems, used to get Shell
     *  @param model Model were PVs will be added
     */
    @SuppressWarnings("nls")
    public AddPVAction(final OperationsManager operations_manager,
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
        runWithSuggestedName(null);
    }

    /** Run the 'add PV' dialog with optional defaults
     *  @param name Suggested PV name, for example from drag-n-drop
     *  @param archive Archive data source for the new PV
     *  @return <code>true</code> if PV name was added, <code>false</code> if canceled by user
     */
    public boolean runWithSuggestedName(final String name)
    {
        // Prompt for PV name
        final String existing_names[] = new String[model.getItemCount()];
        for (int i=0; i<existing_names.length; ++i)
            existing_names[i] = model.getItem(i).getName();
        final String axesList[] = new String[model.getAxesCount()];
        for (int i=0; i<axesList.length; ++i)
        {
            final AxesConfig axes = model.getAxes(i);
            axesList[i] = axes.getName();
        }
        final AddPVDialog dlg = new AddPVDialog(shell, existing_names, axesList, formula);
        dlg.setName(name);
        if (dlg.open() != Window.OK)
            return false;

        Sscan sscan = model.addSscan(name);
        
        // Did user select axis?
        AxesConfig axes;
        if (dlg.getAxisIndex() >= 0)
            axes = model.getAxes(dlg.getAxisIndex());
        else
        {   // Use first empty axis, or create a new one
            axes = model.getEmptyAxes();
            if (axes == null)
                axes = new AddAxesCommand(operations_manager, model).getAxes();
        }

        // Create item
        if (formula)
        {
            final AddModelItemCommand command = AddModelItemCommand.forFormula(
                        shell, operations_manager, model, dlg.getName(), axes);
            if (command == null)
                return false;
            // Open configuration dialog
            final FormulaItem formula = (FormulaItem) command.getItem();
            final EditFormulaDialog edit =
                new EditFormulaDialog(operations_manager, shell, formula);
            edit.open();
        }
        else
            AddModelItemCommand.forPV(shell, operations_manager, model,
                dlg.getName(), axes, sscan);
        return true;
    }
}
