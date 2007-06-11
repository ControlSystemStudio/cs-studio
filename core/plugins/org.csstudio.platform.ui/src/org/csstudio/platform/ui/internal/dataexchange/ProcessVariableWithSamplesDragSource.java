package org.csstudio.platform.ui.internal.dataexchange;

import java.util.ArrayList;
import java.util.Iterator;

//import org.csstudio.platform.model.IProcessVariableWithSample;
import org.csstudio.platform.model.IProcessVariableWithSamples;
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
 *  @see IProcessVariableWithSamples
 *  @see ProcessVariableDragSource
 *  @see SampleDataSourceDragSource
 *  @author Jan Hatje und Helge Rickens
 */
public class ProcessVariableWithSamplesDragSource implements DragSourceListener
{
    /**
     * Switch Debug on / off.
     */
    private static final boolean DEBUG = false;
    /**
     * The selectionProvider.
     */
    private ISelectionProvider _selectionProvider;
    /**
     * The Drag Source.
     */
	private DragSource _source;
    /**
     * List of Data.
     */
    private ArrayList<IProcessVariableWithSamples> _data
        = new ArrayList<IProcessVariableWithSamples>();
	
    /** Create a drag _source for the given GUI item.
     *  <p>
     *  @param control The GUI element from which "drag" should be supported.
     *  @param selectionProvider Interface to whatver provides the current selection
     *  in your application.
     */
	public ProcessVariableWithSamplesDragSource(final Control control,
            final ISelectionProvider selectionProvider)	{
	    this._selectionProvider = selectionProvider;
		_source = new DragSource(control, DND.DROP_COPY);
        _source.setTransfer(new Transfer[]
        {
            ProcessVariableWithSamplesTransfer.getInstance(),
            ProcessVariableNameTransfer.getInstance(),
            TextTransfer.getInstance()
        });
		_source.addDragListener(this);
	}

	/** Used internally by the system to ask if we allow drag-and-drop.
     *  <p>
     *  User should be able to leave this as is.
     *  <p>
     *  Check if there are any archive items in the current selection.
     *  Remember them, or cancel the drag request.
     *  @param event for start Drag
     */
	public final void dragStart(final DragSourceEvent event){
        if (DEBUG) {
            System.out.println("DragStart for PV with Sample");
        }
        _data.clear();
        // Get all archives from the current selection.
        ISelection sel = _selectionProvider.getSelection();
        if (sel instanceof IStructuredSelection){  
            Iterator items = ((IStructuredSelection)sel).iterator();
            while (items.hasNext()){
                Object item = items.next();
                if (DEBUG) {
                    System.out.println("Data: " + item.getClass().getName());
                }
                // Get archive ...
                if (item instanceof IProcessVariableWithSamples) {
                    _data.add((IProcessVariableWithSamples) item);
                } else if (item instanceof IAdaptable){
                    // or adapt to archive
                    IProcessVariableWithSamples sampleItem =
                        (IProcessVariableWithSamples)
                        ((IAdaptable)item).getAdapter(IProcessVariableWithSamples.class);
                    if (item != null) {
                        _data.add(sampleItem);
                    }
                }
                // else: Can't use that item
            }
        }
        if (_data.size() < 1) {
            event.doit = false;
        }
	}

	/** Used by the system to ask for the drag-and-drop _data.
     *  <p>
     *  User should be able to leave as is.
     *  @param event for set Data
     */
	public final void dragSetData(final DragSourceEvent event){
        if (ProcessVariableWithSamplesTransfer.getInstance()
                        .isSupportedType(event.dataType)){
            // Perfect match
            event.data = _data;
        }else if (ProcessVariableNameTransfer.getInstance()
            .isSupportedType(event.dataType)){
            // IProcessVariableNameWithSampleDataSource
            // implies IProcessVariableName
            event.data = _data;
        }else if (TextTransfer.getInstance().isSupportedType(event.dataType)){
            // Get a string
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < _data.size(); i++) {
                if (i > 0){
                	buf.append(", ");
                }
                buf.append(_data.get(i).getName());
            }
			event.data = buf.toString();
		}
	}

	/** Used by the system to tell us that drag-and-drop is done.
     *  <p>
     *  User should be able to leave as is.
     *  @param event for Finished
     */
	public final void dragFinished(final DragSourceEvent event){
		_data.clear();
	}
}
