package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.trends.databrowser.model.FormulaModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.formula_gui.FormulaDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Add a formula via the ConfigView.
 *  @author Kay Kasemir
 */
public class AddFormulaAction extends AbstractAddModelItemAction
{
    final private Shell shell;
    
	public AddFormulaAction(final Shell shell, final Model model)
	{
        super(model);
        this.shell = shell;
		setText(Messages.AddFormula);
		setToolTipText(Messages.AddFormula_TT);
	}
    
    @Override
    protected void addPV(String name)
    {
        try
        {
            final FormulaModelItem item = (FormulaModelItem)
                    model.add(Model.ItemType.Formula, name);
            final FormulaDialog dlg = new FormulaDialog(shell, item);
            dlg.open();
        }
        catch (Throwable ex)
        {
            MessageDialog.openError(shell,
                org.csstudio.trends.databrowser.Messages.ErrorMessageTitle,
                ex.getMessage());
        }
    }
}
