package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.ArchiveDataSource;
import org.csstudio.trends.databrowser.model.PVItem;

/** Undo-able command to delete archive data sources from a PVItem
 *  @author Kay Kasemir
 */
public class DeleteArchiveCommand implements IUndoableCommand
{
    final private PVItem pv;
    final private ArchiveDataSource archives[];

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pv PV from which to delete archives
     *  @param archives Archive data sources to remove
     */
    public DeleteArchiveCommand(final OperationsManager operations_manager,
            final PVItem pv, final ArchiveDataSource archives[])
    {
        this.pv = pv;
        this.archives = archives;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    public void redo()
    {
        pv.removeArchiveDataSource(archives);
    }

    /** {@inheritDoc} */
    public void undo()
    {
        pv.addArchiveDataSource(archives);
    }
    
    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.DeleteArchive;
    }
}
