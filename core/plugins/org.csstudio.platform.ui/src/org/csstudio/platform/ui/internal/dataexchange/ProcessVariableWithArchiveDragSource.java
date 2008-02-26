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

import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/** Support dragging a PV Name and Archive Data Source info out of a GUI item.
 *  <p>
 *  @see IProcessVariableWithArchive
 *  @see ProcessVariableDragSource
 *  @see ArchiveDataSourceDragSource
 *  @author Kay Kasemir
 */
public class ProcessVariableWithArchiveDragSource implements DragSourceListener
{
    private static final boolean debug = true;
    private ISelectionProvider selection_provider;
	private DragSource source;
    private ArrayList<IProcessVariableWithArchive> data
        = new ArrayList<IProcessVariableWithArchive>();
	
    /** Create a drag source for the given GUI item.
     *  <p>
     *  @param control The GUI element from which "drag" should be supported.
     *  @param selection_provider Interface to whatver provides the current selection
     *  in your application.
     */
	public ProcessVariableWithArchiveDragSource(Control control,
            ISelectionProvider selection_provider)
	{
	    this.selection_provider = selection_provider;
		source = new DragSource(control, DND.DROP_COPY);
        source.setTransfer(new Transfer[]
        {
            ProcessVariableWithArchiveTransfer.getInstance(),
            ProcessVariableNameTransfer.getInstance(),
            ArchiveDataSourceTransfer.getInstance(),
            TextTransfer.getInstance()
        });
		source.addDragListener(this);
	}

	/** Used internally by the system to ask if we allow drag-and-drop.
     *  <p>
     *  User should be able to leave this as is.
     *  <p>
     *  Check if there are any archive items in the current selection.
     *  Remember them, or cancel the drag request.
     */
	public void dragStart(DragSourceEvent event)
	{
        if (debug)
            System.out.println("DragStart for PV with Archive");
        data.clear();
        // Get all archives from the current selection.
        ISelection sel = selection_provider.getSelection();
        if (sel instanceof IStructuredSelection)
        {  
            Iterator items = ((IStructuredSelection)sel).iterator();
            while (items.hasNext())
            {
                Object item = items.next();
                if (debug)
                    System.out.println("Data: " + item.getClass().getName());
                // Get archive ...
                if (item instanceof IProcessVariableWithArchive)
                    data.add((IProcessVariableWithArchive) item);
                else if (item instanceof IAdaptable)
                {   // or adapt to archive
                    IProcessVariableWithArchive archive_item =
                        (IProcessVariableWithArchive)
                        ((IAdaptable)item).getAdapter(IProcessVariableWithArchive.class);
                    if (item != null)
                        data.add(archive_item);
                }
                // else: Can't use that item
            }
        }
        if (data.size() < 1)
            event.doit = false;
	}

	/** Used by the system to ask for the drag-and-drop data.
     *  <p>
     *  User should be able to leave as is.
     */
	public void dragSetData(DragSourceEvent event)
	{
        if (ProcessVariableWithArchiveTransfer.getInstance()
                        .isSupportedType(event.dataType))
        {   // Perfect match
            event.data = data;
        }
        else if (ProcessVariableNameTransfer.getInstance()
            .isSupportedType(event.dataType))
        {   // IProcessVariableNameWithArchiveDataSource
            // implies IProcessVariableName
            event.data = data;
        }
        else if (ArchiveDataSourceTransfer.getInstance().
                        isSupportedType(event.dataType))
        {
            // Get the IArchiveDataSource
            ArrayList<IArchiveDataSource> archives =
                new ArrayList<IArchiveDataSource>(data.size());
            for (int i = 0; i < data.size(); i++)
                archives.add(data.get(i).getArchiveDataSource());
            event.data = archives;
        }
        else if (TextTransfer.getInstance().isSupportedType(event.dataType))
		{   // Get a string
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < data.size(); i++)
			{
				if (i > 0)
					buf.append(", ");
                buf.append(data.get(i).getName());
				IArchiveDataSource arch = data.get(i).getArchiveDataSource();
                buf.append(" [ " + arch.getUrl() + ", "
                                + arch.getKey() + ", "
                                + arch.getName() + " ]");
			}
			event.data = buf.toString();
		}
	}

	/** Used by the system to tell us that drag-and-drop is done.
     *  <p>
     *  User should be able to leave as is.
     */
	public void dragFinished(DragSourceEvent event)
	{
		data.clear();
	}
}
