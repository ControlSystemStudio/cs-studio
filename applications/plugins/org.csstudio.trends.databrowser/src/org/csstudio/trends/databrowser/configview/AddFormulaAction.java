package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.AbstractAddPVAction;
import org.eclipse.jface.dialogs.MessageDialog;

/** Add a formula via the ConfigView.
 *  @author Kay Kasemir
 */
public class AddFormulaAction extends AbstractAddPVAction
{
    final private ConfigView config;

	public AddFormulaAction(ConfigView config)
	{
        super();
        this.config = config;
		setText(Messages.AddFormula);
		setToolTipText(Messages.AddFormula_TT);
        setEnabled(false);
	}
    
    @Override
    protected void addPV(String name)
    {
        // TODO remove when done
        MessageDialog.openWarning(config.getSite().getShell(),
                        "Warning", "Formulas are not yet functional!");

        config.addFormula(name);
    }
}
