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
 *  (Using the design of the datatype implementations from Kay Kasemir)
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
