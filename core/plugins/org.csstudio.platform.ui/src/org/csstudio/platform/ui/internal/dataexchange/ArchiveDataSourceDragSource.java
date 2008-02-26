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

/** Support dragging Archive Data Source info out of a GUI item.
 *  <p>
 *  The double "source" in the name sounds silly,
 *  but note that this is a drag source (1)
 *  for archive data source (2) information, i.e. where to get history data,
 *  not a source of archive data, i.e. actual samples.
 *  <p>
 *  The user of this class has to provide the following:
 *  <ol>
 *  <li>A GUI item from which to drag data, for example a 'Table' or 'Tree'
 *      widget, but any SWT Control would work.
 *  <li>A selection provider that implements or adapts to
 *      IArchiveDataSource.
 *      In the table or tree example, that can be the associated JFace
 *      TableViewer or TreeViewer. In other cases, you might have to
 *      implement an ISelectionProvider.
 *  </ol>
 *  @author Kay Kasemir
 */
public class ArchiveDataSourceDragSource implements DragSourceListener
{
    private ISelectionProvider selection_provider;
	private DragSource source;
    private ArrayList<IArchiveDataSource> archives
        = new ArrayList<IArchiveDataSource>();
	
    /** Create an archive data source drag source for the given GUI item.
     *  <p>
     *  The current selection should implement or adapt to IArchiveDataSource,
     *  because that is how the info to 'drag' will be requested
     *  from the current selection.
     *  <p>
     *  @param control The GUI element from which "drag" should be supported.
     *  @param selection_provider Interface to whatver provides the current selection
     *  in your application.
     */
	public ArchiveDataSourceDragSource(Control control,
            ISelectionProvider selection_provider)
	{
	    this.selection_provider = selection_provider;
		source = new DragSource(control, DND.DROP_COPY);
        source.setTransfer(new Transfer[]
        {
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
        archives.clear();
        // Get all archives from the current selection.
        ISelection sel = selection_provider.getSelection();
        if (sel instanceof IStructuredSelection)
        {  
            Iterator items = ((IStructuredSelection)sel).iterator();
            while (items.hasNext())
            {
                Object item = items.next();
                // Get archive ...
                if (item instanceof IArchiveDataSource)
                    archives.add((IArchiveDataSource) item);
                else if (item instanceof IAdaptable)
                {   // or adapt to archive
                    IArchiveDataSource archive_item =
                        (IArchiveDataSource)
                        ((IAdaptable)item).getAdapter(IArchiveDataSource.class);
                    if (item != null)
                        archives.add(archive_item);
                }
                // else: Can't use that item
            }
        }
        if (archives.size() < 1)
            event.doit = false;
	}

	/** Used by the system to ask for the drag-and-drop data.
     *  <p>
     *  User should be able to leave as is.
     */
	public void dragSetData(DragSourceEvent event)
	{
        if (ArchiveDataSourceTransfer.getInstance()
            .isSupportedType(event.dataType))
        {
            event.data = archives;
        }
        else if (TextTransfer.getInstance().isSupportedType(event.dataType))
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < archives.size(); i++)
			{
				if (i > 0)
					buf.append(", ");
				IArchiveDataSource arch = archives.get(i);
                buf.append("[ " + arch.getUrl() + ", "
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
		archives.clear();
	}
}
