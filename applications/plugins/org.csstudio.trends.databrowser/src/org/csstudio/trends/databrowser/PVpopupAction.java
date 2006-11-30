package org.csstudio.trends.databrowser;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;

/** Another application sent us a PV name via its popup menu. */
public class PVpopupAction extends ProcessVariablePopupAction
{    
    public void handlePVs(IProcessVariable pv_names[])
    {
    	PlotEditor editor = PlotEditor.createChartEditor();
    	if (editor == null)
    		return;
        Controller controller = editor.getController();
        for (IProcessVariable pv : pv_names)
            controller.add(pv.getName());
    }
}
