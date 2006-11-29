package org.csstudio.platform.ui.internal.data.exchange;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Support receiving Process Variable names via Drag-and-drop.
 *  <p>
 *  This drop target will receive ProcessVariableNameTransfer data.
 *  <p>
 *  Whenever a PV is received, the handleDrop callback is received.
 *  In the case of ProcessVariableNameTransfer, multiple callbacks
 *  result 'almost at once' when multiple PVs were dropped.
 *  <p>
 *  User needs to provide the GUI element onto which a 'drop' should
 *  be allowed.
 *  @author Kay Kasemir
 */
public abstract class ProcessVariableDropTarget extends DropTargetAdapter
{
	private DropTarget target;
	
    /** Cause that 'target' GUI element to accept PV names via Drag-and-drop.
     *  @param target The SWT GUI element.
     */
	public ProcessVariableDropTarget(Control target)
	{
		this.target = new DropTarget(target, DND.DROP_MOVE | DND.DROP_COPY);
		this.target.setTransfer(new Transfer[]
        {
            ProcessVariableNameTransfer.getInstance()
        });
		this.target.addDropListener(this);
	}
    
    /** Callback for each dropped PV.
     *  @param name The dropped PV name
     *  @param event The original event, in case you need e.g. the x/y position.
     */
    public abstract void handleDrop(IProcessVariable name,
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
		IProcessVariable names[] = (IProcessVariable [])event.data;
        for (int i = 0; i < names.length; i++)
            handleDrop(names[i], event);
	}
}
