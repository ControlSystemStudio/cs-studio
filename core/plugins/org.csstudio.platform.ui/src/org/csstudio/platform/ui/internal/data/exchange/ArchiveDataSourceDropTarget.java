package org.csstudio.platform.ui.internal.data.exchange;

import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Support receiving Archive Data Source info via Drag-and-drop.
 *  <p>
 *  This drop target will receive <code>ArchiveDataSourceTransfer</code> data.
 *  <p>
 *  Whenever an item received, the handleDrop callback is received.
 *  In the case of <code>ArchiveDataSourceTransfer</code>, multiple callbacks
 *  result 'almost at once' when multiple items were dropped.
 *  <p>
 *  User needs to provide the GUI element onto which a 'drop' should
 *  be allowed.
 *  @author Kay Kasemir
 */
public abstract class ArchiveDataSourceDropTarget extends DropTargetAdapter
{
	private DropTarget target;
	
    /** Cause that 'target' GUI element to accept Archive Data Source info
     *  via Drag-and-drop.
     *  @param target The SWT GUI element.
     */
	public ArchiveDataSourceDropTarget(Control target)
	{
		this.target = new DropTarget(target, DND.DROP_MOVE | DND.DROP_COPY);
		this.target.setTransfer(new Transfer[]
        {
            ArchiveDataSourceTransfer.getInstance()
        });
		this.target.addDropListener(this);
	}
    
    /** Callback for a dropped archive data source.
     *  @param archive The dropped archive info
     *  @param event The original event in case you need details.
     */
    public abstract void handleDrop(IArchiveDataSource archive,
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
        IArchiveDataSource archives[] = (IArchiveDataSource [])event.data;
        for (int i = 0; i < archives.length; i++)
            handleDrop(archives[i], event);
	}
}
