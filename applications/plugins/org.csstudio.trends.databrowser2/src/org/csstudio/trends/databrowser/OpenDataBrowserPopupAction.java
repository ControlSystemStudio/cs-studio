package org.csstudio.trends.databrowser;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.trends.databrowser.editor.DataBrowserEditor;
import org.csstudio.trends.databrowser.model.ArchiveDataSource;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;

/** Object contribution registered in plugin.xml for context menus with 
 *  IProcessVariable in the current selection.
 *  @author Kay Kasemir
 */
public class OpenDataBrowserPopupAction extends ProcessVariablePopupAction
{
    /** {@inheritDoc} */
    @Override
    public void handlePVs(final IProcessVariable pvNames[])
    {
        // Create new editor
        final DataBrowserEditor editor = DataBrowserEditor.createInstance();
        if (editor == null)
            return;
        final Model model = editor.getModel();
        final double period = Preferences.getScanPeriod();
        try
        {
            // Add received PVs
            for (IProcessVariable pv : pvNames)
            {
                final PVItem item = new PVItem(pv.getName(), period);
                if (pv instanceof IProcessVariableWithArchive)
                {   // Use received archive
                    final IArchiveDataSource archive =
                        ((IProcessVariableWithArchive) pv).getArchiveDataSource();
                    item.addArchiveDataSource(new ArchiveDataSource(archive));
                }
                else
                    item.useDefaultArchiveDataSources();
                // Add items to new axes
                item.setAxis(model.addAxis());
                model.addItem(item);
            }
        }
        catch (Exception ex)
        {
            MessageDialog.openError(editor.getSite().getShell(),
                    Messages.Error,
                    NLS.bind(Messages.ErrorFmt, ex.getMessage()));
        }
    }
}
