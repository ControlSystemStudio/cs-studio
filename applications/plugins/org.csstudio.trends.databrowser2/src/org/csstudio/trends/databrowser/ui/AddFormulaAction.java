package org.csstudio.trends.databrowser.ui;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.FormulaItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.propsheet.EditFormulaDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

/** Context menu action that adds a Formula to the Model
 *  @author Kay Kasemir
 */
public class AddFormulaAction extends Action
{
    final private OperationsManager operations_manager;
    final private Shell shell;
    final private Model model;
    
    /** Initialize
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param trace_table Table of ModelItems, used to get Shell
     *  @param model Model were Formula will be added
     */
    public AddFormulaAction(final OperationsManager operations_manager,
            final Shell shell, final Model model)
    {
        super(Messages.AddFormula,
              Activator.getDefault().getImageDescriptor("icons/add_formula.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.shell = shell;
        this.model = model;
    }

    @Override
    public void run()
    {
        // Prompt for PV name
        final String existing_names[] = new String[model.getItemCount()];
        for (int i=0; i<existing_names.length; ++i)
            existing_names[i] = model.getItem(i).getName();
        final AddFormulaDialog dlg = new AddFormulaDialog(shell, existing_names);
        if (dlg.open() != AddFormulaDialog.OK)
            return;
        // Add the new formula
        final AddModelItemCommand command = AddModelItemCommand.forFormula(shell, operations_manager, model, dlg.getName());
        if (command == null)
            return;
        // Open configuration dialog
        final FormulaItem formula = (FormulaItem) command.getItem();
        final EditFormulaDialog edit =
            new EditFormulaDialog(operations_manager, shell, formula);
        edit.open();
    }
}
