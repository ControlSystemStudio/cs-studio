package org.csstudio.sds.ui.internal.editor.dnd;

import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.ui.util.dnd.SerializableItemTransfer;
import org.eclipse.gef.EditPartViewer;

/**
 * Drag source listener for SDS that provides a single {@link ProcessVariable}.
 *
 * @author swende
 *
 */
public class ProcessVariableDragSourceListener extends AbstractDragSourceListener<ProcessVariable> {

    public ProcessVariableDragSourceListener(EditPartViewer viewer) {
        super(viewer, SerializableItemTransfer.getTransfer(ProcessVariable.class));
    }

    @Override
    protected ProcessVariable convert(IProcessVariableAddress mainAddress, List<IProcessVariableAddress> allAddresses) {
        return mainAddress != null ? new ProcessVariable(mainAddress.getProperty()) : null;
    }

}
