package org.csstudio.trends.databrowser;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.csstudio.trends.databrowser.preferences.Preferences;

/** Another application sent us a PV name via its popup menu.
 *  @author Kay Kasemir
 */
public class OpenEditorWithPVsPopupAction extends ProcessVariablePopupAction
{    
    @Override
    public void handlePVs(final IProcessVariable pv_names[])
    {
        final PlotEditor editor = PlotEditor.createInstance();
    	if (editor == null)
    		return;
        final Model model = editor.getModel();
        addPVsAndArchives(model, pv_names);
    }

    /** Add the given PVs to the model. */
    protected void addPVsAndArchives(final Model model,
                                     final IProcessVariable[] pv_names)
    {
        for (IProcessVariable pv : pv_names)
        {   // Add every received PV to the model
            IPVModelItem item = model.addPV(pv.getName());
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
