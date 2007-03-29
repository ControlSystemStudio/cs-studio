package org.csstudio.platform.ui.internal.dataexchange;

import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.platform.model.IProcessVariableWithSample;
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

/** Support dragging a PV Name and Sample Data Source info out of a GUI item.
 *  <p>
 *  @see IProcessVariableWithSample
 *  @see ProcessVariableDragSource
 *  @see SampleDataSourceDragSource
 *  @author Jan Hatje und Helge Rickens
 */
public class ProcessVariableWithSampleDragSource implements DragSourceListener
{
    private static final boolean debug = true;
    public ISelectionProvider selection_provider;
	private DragSource source;
    public ArrayList<IProcessVariableWithSample> data
        = new ArrayList<IProcessVariableWithSample>();
	
    /** Create a drag source for the given GUI item.
     *  <p>
     *  @param control The GUI element from which "drag" should be supported.
     *  @param selection_provider Interface to whatver provides the current selection
     *  in your application.
     */
	public ProcessVariableWithSampleDragSource(Control control,
            ISelectionProvider selection_provider)
	{
	    this.selection_provider = selection_provider;
		source = new DragSource(control, DND.DROP_COPY);
        source.setTransfer(new Transfer[]
        {
            ProcessVariableWithSampleTransfer.getInstance(),
            ProcessVariableNameTransfer.getInstance(),
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
            System.out.println("DragStart for PV with Sample");
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
                if (item instanceof IProcessVariableWithSample)
                    data.add((IProcessVariableWithSample) item);
                else if (item instanceof IAdaptable)
                {   // or adapt to archive
                    IProcessVariableWithSample sample_item =
                        (IProcessVariableWithSample)
                        ((IAdaptable)item).getAdapter(IProcessVariableWithSample.class);
                    if (item != null)
                        data.add(sample_item);
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
        if (ProcessVariableWithSampleTransfer.getInstance()
                        .isSupportedType(event.dataType))
        {   // Perfect match
            event.data = data;
        }
        else if (ProcessVariableNameTransfer.getInstance()
            .isSupportedType(event.dataType))
        {   // IProcessVariableNameWithSampleDataSource
            // implies IProcessVariableName
            event.data = data;
        }
        else if (TextTransfer.getInstance().isSupportedType(event.dataType))
		{   // Get a string
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < data.size(); i++)
			{
				if (i > 0)
					buf.append(", ");
                buf.append(data.get(i).getName());
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
