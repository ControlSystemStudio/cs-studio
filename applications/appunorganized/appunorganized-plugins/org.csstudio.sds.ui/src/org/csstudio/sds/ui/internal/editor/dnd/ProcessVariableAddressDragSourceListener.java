package org.csstudio.sds.ui.internal.editor.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.ui.util.dnd.SerializableItemTransfer;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.dnd.DragSourceEvent;

/**
 * Drag source listener for SDS that provides a list of
 * {@link IProcessVariableAddress}.
 *
 * @author swende
 *
 */
public class ProcessVariableAddressDragSourceListener extends AbstractTransferDragSourceListener {

    public ProcessVariableAddressDragSourceListener(EditPartViewer viewer) {
        super(viewer, SerializableItemTransfer.getTransfer(ProcessVariable.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void dragSetData(DragSourceEvent event) {
        List<ProcessVariable> pvs = new ArrayList<ProcessVariable>();
        List<AbstractEditPart> controllers = getViewer().getSelectedEditParts();

        for (AbstractEditPart c : controllers) {
//            if (c instanceof ProcessVariable) {
//                pvs.add((ProcessVariable) c);
//            }
        }

        if (!pvs.isEmpty()) {
            event.doit = true;
            event.data = pvs;
        } else {
            event.doit = false;
        }
    }

}
