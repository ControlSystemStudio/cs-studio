package org.csstudio.trends.databrowser.configview;

import org.csstudio.trends.databrowser.AbstractAddPVAction;

/** Action that adds a new PV to the model.
 *  @author Kay Kasemir
 */
public class AddPVAction extends AbstractAddPVAction
{
    final private ConfigView config;
	
	AddPVAction(ConfigView config)
	{
        super();
		this.config = config;
        setEnabled(false);
	}

    @Override
    protected void addPV(String pv_name)
    {
        config.addPV(pv_name);
    }
}
