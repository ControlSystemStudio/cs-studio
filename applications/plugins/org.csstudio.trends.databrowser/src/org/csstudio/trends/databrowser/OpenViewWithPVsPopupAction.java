package org.csstudio.trends.databrowser;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.plotview.PlotView;

/** Another application sent us a PV name via its popup menu.
 *  @author Kay Kasemir
 */
public class OpenViewWithPVsPopupAction extends OpenEditorWithPVsPopupAction
{    
    @Override
    public void handlePVs(final IProcessVariable pv_names[])
    {
        final PlotView view = PlotView.createInstance();
        
    	if (view == null)
    		return;
        final Model model = view.getModel();
        addPVsAndArchives(model, pv_names);
    }
}
