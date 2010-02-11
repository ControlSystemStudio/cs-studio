package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.PVItem;
import org.csstudio.trends.databrowser.search.AddArchiveDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

/** Action that allows addition of archive data sources to PVs.
 *  @author Kay Kasemir
 */
public class AddArchiveAction extends Action
{
    final private OperationsManager operations_manager;
    final private Shell shell;
    final private PVItem pvs[];

    /** Initialize
     *  @param shell Parent shell for dialog
     *  @param pvs PVs to which to add archives
     */
    public AddArchiveAction(final OperationsManager operations_manager,
            final Shell shell, final PVItem pvs[])
    {
        super(Messages.AddArchive,
              Activator.getDefault().getImageDescriptor("icons/archive.gif")); //$NON-NLS-1$
        this.operations_manager = operations_manager;
        this.shell = shell;
        this.pvs = pvs;
    }

    @Override
    public void run()
    {
        final AddArchiveDialog dlg = new AddArchiveDialog(shell);
        if (dlg.open() != AddArchiveDialog.OK)
            return;
        new AddArchiveCommand(operations_manager, pvs, dlg.getArchives());
    }
}
