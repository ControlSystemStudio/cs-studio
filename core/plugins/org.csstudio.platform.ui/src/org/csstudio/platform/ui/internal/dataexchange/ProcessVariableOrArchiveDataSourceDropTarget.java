/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
