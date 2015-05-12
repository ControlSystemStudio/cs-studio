package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.ui.editor.copyandpaste.AbstractElementTransfer;
import org.csstudio.dct.ui.editor.copyandpaste.ICopyAndPasteStrategy;
import org.csstudio.dct.ui.editor.copyandpaste.InstanceTransfer;
import org.csstudio.dct.ui.editor.copyandpaste.PrototypeTransfer;
import org.csstudio.dct.ui.editor.copyandpaste.RecordTransfer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;

public class PasteElementAction extends AbstractOutlineAction {
    private AbstractElementTransfer[] transferTypes = new AbstractElementTransfer[] { RecordTransfer.getInstance(),
            PrototypeTransfer.getInstance(), InstanceTransfer.getInstance() };

    public PasteElementAction() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Command createCommand(List<IElement> selection) {
        Clipboard clipboard = new Clipboard(Display.getCurrent());

        for (TransferData td : clipboard.getAvailableTypes()) {
            for (AbstractElementTransfer transfer : transferTypes) {
                if (transfer.isSupportedType(td)) {
                    Command result = transfer.getCopyAndPasteStrategy().createPasteCommand((List<IElement>) clipboard.getContents(transfer),
                            getProject(), selection);

                    return result;
                }
            }
        }
        return null;
    }

    @Override
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
        assert selection != null;

        boolean enabled = false;
        String comment = null;

        Clipboard clipboard = new Clipboard(Display.getCurrent());

        for (TransferData td : clipboard.getAvailableTypes()) {
            for (AbstractElementTransfer transfer : transferTypes) {
                if (!enabled) {
                    if (transfer.isSupportedType(td)) {
                        ICopyAndPasteStrategy s = transfer.getCopyAndPasteStrategy();
                        enabled = s.canPaste(selection);
                        comment = s.getContentDescription();
                    }
                }
            }
        }

        action.setEnabled(enabled);

        if (action.getToolTipText() == null && action.getText() != null) {
            action.setToolTipText(action.getText());
        }

        if (action.getToolTipText() != null) {
            action.setText(action.getToolTipText() + (comment != null ? (" (" + comment + ")") : ""));
        }
    }
}
