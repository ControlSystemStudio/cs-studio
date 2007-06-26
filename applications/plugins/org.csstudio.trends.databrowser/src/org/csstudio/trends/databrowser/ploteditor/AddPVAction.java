package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.AbstractAddPVAction;
import org.csstudio.trends.databrowser.model.IPVModelItem;
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

    @Override
    protected void addPV(String pv_name)
    {
        IPVModelItem pv_item = model.addPV(pv_name);
        model.addDefaultArchiveSources(pv_item);
    }
}
