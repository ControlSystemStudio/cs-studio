package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.AbstractAddPVAction;
import org.csstudio.trends.databrowser.model.Model;

/** Action that adds a new PV to the model.
 *  @author Kay Kasemir
 */
public class AddPVAction extends AbstractAddPVAction
{
    private Model model;
	
	public AddPVAction(Model model)
	{
        super();
		this.model = model;
	}

    protected void addPV(String pv_name)
    {
        model.add(pv_name);
    }
}
