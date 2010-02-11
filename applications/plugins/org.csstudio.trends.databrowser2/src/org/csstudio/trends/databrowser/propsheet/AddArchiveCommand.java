package org.csstudio.trends.databrowser.propsheet;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser.Messages;
import org.csstudio.trends.databrowser.model.ArchiveDataSource;
import org.csstudio.trends.databrowser.model.PVItem;

/** Undo-able command to add archive data sources to a PVItem
 *  @author Kay Kasemir
 */
public class AddArchiveCommand implements IUndoableCommand
{
    final private PVItem pvs[];
    final private ArchiveDataSource archives[];

    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pv PV where to add archive
     *  @param archive Archive data source to add
     */
    public AddArchiveCommand(final OperationsManager operations_manager,
            final PVItem pv, final ArchiveDataSource archive)
    {
        this(operations_manager, new PVItem[] { pv }, new ArchiveDataSource[] { archive });
    }
    
    /** Register and perform the command
     *  @param operations_manager OperationsManager where command will be reg'ed
     *  @param pvs PVs where to add archives
     *  @param archives Archive data sources to add
     */
    public AddArchiveCommand(final OperationsManager operations_manager,
            final PVItem pvs[], final ArchiveDataSource archives[])
    {
        this.pvs = pvs;
        this.archives = archives;
        operations_manager.addCommand(this);
        redo();
    }

    /** {@inheritDoc} */
    public void redo()
    {
        for (PVItem pv : pvs)
            pv.addArchiveDataSource(archives);
    }

    /** {@inheritDoc} */
    public void undo()
    {
        for (PVItem pv : pvs)
            pv.removeArchiveDataSource(archives);
    }
    
    /** @return Command name that appears in undo/redo menu */
    @Override
    public String toString()
    {
        return Messages.AddArchive;
    }
}
