package org.csstudio.platform.ui.internal.dataexchange;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Support receiving Process Variable names or Archive Data Sources
 *  or both via Drag-and-drop.
 *  <p>
 *  @see ProcessVariableDropTarget
 *  @see ArchiveDataSourceDropTarget
 *  @author Kay Kasemir
 */
public abstract class ProcessVariableOrArchiveDataSourceDropTarget extends DropTargetAdapter
{
	private DropTarget target;
	
    /** Cause that 'target' GUI element to accept PV names and/or
     *  archive data sources via Drag-and-drop.
     *  @param target The SWT GUI element.
     */
	public ProcessVariableOrArchiveDataSourceDropTarget(Control target)
	{
		this.target = new DropTarget(target, DND.DROP_MOVE | DND.DROP_COPY);
		this.target.setTransfer(new Transfer[]
        {   // Order matters:
            // Ideally, we receive PV-with-Archive:
            ProcessVariableWithArchiveTransfer.getInstance(),
            // Less desirable alternatives follow:
            ProcessVariableNameTransfer.getInstance(),
            ArchiveDataSourceTransfer.getInstance()
        });
		this.target.addDropListener(this);
	}
    
    /** Callback for each dropped PV.
     *  Gets invoked if just a PV was dropped, without archive data.
     *  @param name The dropped PV name
     *  @param event The original event, in case you need e.g. the x/y position.
     */
    public abstract void handleDrop(IProcessVariable name,
                                    DropTargetEvent event);

    /** Callback for a dropped archive data source.
     *  Gets invoked if just a data source was dropped, without PV.
     *  @param archive The dropped archive info
     *  @param event The original event in case you need details.
     */
    public abstract void handleDrop(IArchiveDataSource archive,
                                    DropTargetEvent event);
    
    /** Callback for a dropped PV with archive data source.
     *  Gets invoked if a PV together with data source was dropped.
     *  @param name The dropped PV name
     *  @param archive The dropped archive info
     *  @param event The original event in case you need details.
     */
    public abstract void handleDrop(IProcessVariable name,
                                    IArchiveDataSource archive,
                                    DropTargetEvent event);
    
	/** Used internally by the system when a DnD operation enters the control.
     *  @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dragEnter(DropTargetEvent event)
	{
		if ((event.operations & DND.DROP_COPY) != 0)
			event.detail = DND.DROP_COPY;
		else
			event.detail = DND.DROP_NONE;
	}

    /** Used internally by the system to drop the data.
     *  @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */
	public void drop(DropTargetEvent event)
	{
        if (event.data instanceof IProcessVariableWithArchive[])
        {
            IProcessVariableWithArchive names[] = (IProcessVariableWithArchive [])event.data;
            for (int i = 0; i < names.length; i++)
                handleDrop(names[i], names[i].getArchiveDataSource(), event);
        }
        else if (event.data instanceof IProcessVariable[])
        {
            IProcessVariable names[] = (IProcessVariable [])event.data;
            for (int i = 0; i < names.length; i++)
                handleDrop(names[i], event);
        }
        else if (event.data instanceof IArchiveDataSource[])
        {
            IArchiveDataSource archives[] = (IArchiveDataSource [])event.data;
            for (int i = 0; i < archives.length; i++)
                handleDrop(archives[i], event);
        }
	}
}
