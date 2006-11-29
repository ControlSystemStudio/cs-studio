package org.csstudio.platform.ui.internal.dataexchange.demo;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Simple text (string) drop target
 *  @author Kay Kasemir
 */
public abstract class StringDropTarget extends DropTargetAdapter
{
	private DropTarget target;
	
    /** Cause that 'target' GUI element to accept strings via Drag-and-drop.
     *  @param target The SWT GUI element.
     */
	public StringDropTarget(Control target)
	{
		this.target = new DropTarget(target, DND.DROP_MOVE | DND.DROP_COPY);
		this.target.setTransfer(new Transfer[]
        {
            TextTransfer.getInstance()
        });
		this.target.addDropListener(this);
	}
    
    /** Callback for a dropped String. */
    public abstract void handleDrop(String text);

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
	    if (TextTransfer.getInstance()
                        .isSupportedType(event.currentDataType))
            handleDrop(event.data.toString());
	}
}
