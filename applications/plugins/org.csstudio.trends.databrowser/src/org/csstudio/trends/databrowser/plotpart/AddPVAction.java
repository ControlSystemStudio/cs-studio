package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.dialogs.MessageDialog;

/** Action that adds a new PV to the model.
 *  <p>
 *  The actual model can change when this action
 *  is used within a config. view with changing models,
 *  including model==null, which disables the action.
 *  @author Kay Kasemir
 */
public class AddPVAction extends AbstractAddModelItemAction
{
    /** Constructor */
	public AddPVAction(final Model model)
	{
	    super(model);
        setText(Messages.AddPV);
        setToolTipText(Messages.AddPV_TT);
	}

	@Override
    protected void addPV(final String pv_name)
    {
	    try
	    {
	        final IPVModelItem pv_item = model.addPV(pv_name);
	        model.addDefaultArchiveSources(pv_item);
	    }
	    catch (Throwable ex)
	    {
	        Plugin.getLogger().error(ex);
            MessageDialog.openError(null, "Error", ex.getMessage()); //$NON-NLS-1$
	    }
    }
}
