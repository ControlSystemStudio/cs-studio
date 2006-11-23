/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui.dnd;

import org.csstudio.platform.ui.internal.dnd.ControlSystemItemTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/**
 * Utility class, which provides methods to prepare SWT controls for drag or
 * drop operation.
 * 
 * @author Sven Wende
 * 
 */
public final class DnDUtil {

	/**
	 * Private constructor to prevent instantiation of this utitlity class.
	 */
	private DnDUtil() {
	}

	/**
	 * Prepares the specified control for drop operations.
	 * 
	 * @param control
	 *            the <code>Control</code> over which the user positions the
	 *            cursor to drop the data
	 * @param style
	 *            the bitwise OR'ing of allowed operations; this may be a
	 *            combination of any of DND.DROP_NONE, DND.DROP_COPY,
	 *            DND.DROP_MOVE, DND.DROP_LINK (further details can be found in
	 *            {@link DND})
	 * @param dropTargetAdapter
	 *            the drop listener who will be notified when a drag and drop
	 *            operation is in progress, by sending it one of the messages
	 *            defined in the <code>DropTargetListener</code> interface.
	 */
	public static void enableForDrop(final Control control, final int style,
			final FilteredDropTargetAdapter dropTargetAdapter) {
		DropTarget dropTarget = new DropTarget(control, style);

		dropTarget.setTransfer(new Transfer[] {
				ControlSystemItemTransfer.getInstance(),
				TextTransfer.getInstance()});

		dropTarget.addDropListener(dropTargetAdapter);
	}

	/**
	 * Prepares the specified control for drop operations.
	 * 
	 * @param control
	 *            the <code>Control</code> that the user clicks on to initiate
	 *            the drag
	 * @param style
	 *            the bitwise OR'ing of allowed operations; this may be a
	 *            combination of any of DND.DROP_NONE, DND.DROP_COPY,
	 *            DND.DROP_MOVE, DND.DROP_LINK (further details can be found in
	 *            {@link DND})
	 * 
	 * @param dragSourceAdapter
	 *            the listener who will be notified when a drag and drop
	 *            operation is in progress, by sending it one of the messages
	 *            defined in the <code>DragSourceListener</code> interface.
	 */
	public static void enableForDrag(final Control control, final int style,
			final FilteredDragSourceAdapter dragSourceAdapter) {

		DragSource dragSource = new DragSource(control, style);

		Transfer[] types = new Transfer[] {
				ControlSystemItemTransfer.getInstance(),
				TextTransfer.getInstance() };

		dragSource.setTransfer(types);

		dragSource.addDragListener(dragSourceAdapter);
	}

}
