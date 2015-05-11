package org.csstudio.sds.ui.internal.editor.dnd;

import java.util.Collections;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

abstract class AbstractDragSourceListener<E> extends
        AbstractTransferDragSourceListener {

    public AbstractDragSourceListener(EditPartViewer viewer, Transfer transfer) {
        super(viewer, transfer);
    }

    @SuppressWarnings("unchecked")
    public final void dragSetData(DragSourceEvent event) {
        List<AbstractEditPart> controllers = getViewer().getSelectedEditParts();

        E transferDataObject = null;

        for (AbstractEditPart controller : controllers) {
            if (controller instanceof IProcessVariableAdressProvider) {
                IProcessVariableAdressProvider provider = (IProcessVariableAdressProvider) controller;

                transferDataObject = convert(
                        provider.getPVAdress(),
                        provider.getProcessVariableAdresses() != null ? provider
                                .getProcessVariableAdresses()
                                : Collections.EMPTY_LIST);

            }
        }

        event.doit = (transferDataObject != null);
        event.data = transferDataObject;
    }

    protected abstract E convert(IProcessVariableAddress mainAddress,
            List<IProcessVariableAddress> allAddresses);
}
