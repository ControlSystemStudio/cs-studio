package org.csstudio.trends.databrowser;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.csstudio.trends.databrowser.preferences.Preferences;

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
        {   // Add every received PV to the model
            IModelItem item = controller.add(pv.getName());
            // In case the PV includes an archive data source..
            if (pv instanceof IProcessVariableWithArchive)
            {   // use it
                final IArchiveDataSource archive =
                    ((IProcessVariableWithArchive) pv).getArchiveDataSource();
                item.addArchiveDataSource(archive);
            }
            else if (item.getArchiveDataSources().length == 0)
            {   // otherwise, use the default archives.
                IArchiveDataSource archives[] = Preferences.getArchiveDataSources();
                for (int i = 0; i < archives.length; i++)
                    item.addArchiveDataSource(archives[i]);
            }
        }
    }
}
