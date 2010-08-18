package org.csstudio.sds.ui.internal.runmode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * Drag source listener for the Run Mode.
 * 
 * @author swende
 * 
 */
class DragSourceListener extends AbstractTransferDragSourceListener {

	/**
	 * Constructor.
	 * 
	 * @param viewer the graphical viewer
	 */
	public DragSourceListener(EditPartViewer viewer) {
		super(viewer, TextTransfer.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void dragSetData(DragSourceEvent event) {
		Set<IProcessVariableAddress> pvs = new HashSet<IProcessVariableAddress>();

		List<AbstractEditPart> controllers = getViewer().getSelectedEditParts();

		for (AbstractEditPart c : controllers) {
			if (c instanceof AbstractWidgetEditPart) {
				AbstractWidgetModel widget = ((AbstractWidgetEditPart) c)
						.getWidgetModel();
				IProcessVariableAddress pv = widget.getMainPvAdress();

				if (pv != null) {
					pvs.add(pv);
				}
			}
		}

		if (!pvs.isEmpty()) {
			StringBuffer sb = new StringBuffer();

			Iterator<IProcessVariableAddress> it = pvs.iterator();

			while (it.hasNext()) {
				sb.append(it.next().toString());

				if (it.hasNext()) {
					sb.append("\r\n");
				}
			}

			event.doit = true;
			event.data = sb.toString();
		} else {
			event.doit = false;
		}

	}

}
