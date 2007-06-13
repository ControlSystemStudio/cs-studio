package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.AbstractAddPVAction;

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
        config.addFormula(name);
    }
}
