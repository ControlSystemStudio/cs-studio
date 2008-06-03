package org.csstudio.trends.databrowser.plotpart;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that adds a new PV to the model.
 *  <p>
 *  The actual model can change when this action
 *  is used within a config. view with changing models,
 *  including model==null, which disables the action.
 *  @author Kay Kasemir
 */
public class AddPVAction extends AbstractAddModelItemAction
{
    final private Shell shell;

    /** Constructor */
	public AddPVAction(final Shell shell, final Model model)
	{
	    super(model);
        this.shell = shell;
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
            MessageDialog.openError(shell,
                    org.csstudio.trends.databrowser.Messages.ErrorMessageTitle,
                    ex.getMessage());
	    }
    }
}
