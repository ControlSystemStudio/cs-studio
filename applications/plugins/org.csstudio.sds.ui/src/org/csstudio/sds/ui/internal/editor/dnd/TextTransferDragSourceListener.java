package org.csstudio.sds.ui.internal.editor.dnd;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * Drag source listener for SDS that provides a single {@link String}.
 *
 * @author swende
 *
 */
public class TextTransferDragSourceListener extends AbstractDragSourceListener<String> {

    public TextTransferDragSourceListener(EditPartViewer viewer) {
        super(viewer, TextTransfer.getInstance());
    }

    @Override
    protected String convert(IProcessVariableAddress mainAddress, List<IProcessVariableAddress> allAddresses) {
        return mainAddress != null ? mainAddress.getProperty() : null;
    }

}
