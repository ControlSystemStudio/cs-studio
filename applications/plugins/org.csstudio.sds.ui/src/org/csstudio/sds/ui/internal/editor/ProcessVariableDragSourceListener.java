package org.csstudio.sds.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableNameTransfer;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.dnd.DragSourceEvent;

public class ProcessVariableDragSourceListener extends
		AbstractTransferDragSourceListener {

	public ProcessVariableDragSourceListener(EditPartViewer viewer) {
		super(viewer, ProcessVariableNameTransfer.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void dragSetData(DragSourceEvent event) {
		List<IProcessVariable> pvs = new ArrayList<IProcessVariable>();
		List<AbstractEditPart> controllers = getViewer().getSelectedEditParts();

		for (AbstractEditPart c : controllers) {
			if (c instanceof IProcessVariable) {
				pvs.add((IProcessVariable) c);
			}
		}
		
		if (!pvs.isEmpty()) {
			event.doit = true;
			event.data = pvs;
		} else {
			event.doit = false;
		}
	}
	
}
