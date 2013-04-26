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
import org.csstudio.trends.sscan.model.FormulaInput;
import org.csstudio.trends.sscan.model.FormulaItem;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.widgets.Shell;

/** Undo-able command to change a FormulaItem's expression and inputs
 *  @author Kay Kasemir
 */
public class ChangeFormulaCommand implements IUndoableCommand
{
    final private Shell shell;
    final private FormulaItem formula;
    final private String old_expression, new_expression;
    final private FormulaInput old_inputs[], new_inputs[];

    /** Register and perform the command
     *  @param shell Shell used for error dialogs
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param formula Model item to configure
     *  @param expression Formula expression
     *  @param inputs Inputs to formula
     */
    public ChangeFormulaCommand(final Shell shell,
            final OperationsManager operations_manager,
            final FormulaItem formula, final String expression,
            final FormulaInput inputs[])
    {
        this.shell = shell;
        this.formula = formula;
        this.old_expression = formula.getExpression();
        this.old_inputs = formula.getInputs();
        this.new_expression = expression;
        this.new_inputs = inputs;
        try
        {
            formula.updateFormula(new_expression, new_inputs);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
            // Exit before registering for undo because there's nothing to undo
            return;
        }
        if (operations_manager != null)
            operations_manager.addCommand(this);
    }

    /** {@inheritDoc} */
    @Override
    public void redo()
    {
        try
        {
            formula.updateFormula(new_expression, new_inputs);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void undo()
    {
        try
        {
            formula.updateFormula(old_expression, old_inputs);
        }
        catch (Exception ex)
        {
            ExceptionDetailsErrorDialog.openError(shell, Messages.Error, ex);
        }
    }

    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.Formula;
    }
}
