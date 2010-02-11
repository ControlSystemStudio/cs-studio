package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.eclipse.jface.action.Action;

/** Action that configures PVs to use default archive data sources.
 *  @author Kay Kasemir
 */
public class AddDefaultArchivesAction extends Action
{
    final private OperationsManager operations_manager;
    final private PVItem pvs[];

    /** Initialize
     *  @param shell Parent shell for dialog
     *  @param pvs PVs that should use default archives
     */
    public AddDefaultArchivesAction(final OperationsManager operations_manager,
            final PVItem pvs[])
    {
        super(Messages.AddDefaultArchives,
              Activator.getDefault().getImageDescriptor("icons/archive.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.pvs = pvs;
    }

    @Override
    public void run()
    {
        new AddArchiveCommand(operations_manager, pvs, Preferences.getArchives());
    }
}
