package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.ui.editor.copyandpaste.AbstractElementTransfer;
import org.csstudio.dct.ui.editor.copyandpaste.InstanceTransfer;
import org.csstudio.dct.ui.editor.copyandpaste.PrototypeTransfer;
import org.csstudio.dct.ui.editor.copyandpaste.RecordTransfer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

public class CopyElementAction extends AbstractOutlineAction {

    private AbstractElementTransfer[] transferTypes = new AbstractElementTransfer[] { RecordTransfer.getInstance(),
            PrototypeTransfer.getInstance(), InstanceTransfer.getInstance() };

    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        assert !selection.isEmpty();

        Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());

        for (AbstractElementTransfer transfer : transferTypes) {
            if (transfer.getCopyAndPasteStrategy().canCopy(selection)) {
                clipboard.setContents(new Object[] { selection }, new Transfer[] { transfer });
                return null;
            }
        }
        return null;
    }

    @Override
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
        boolean enabled = false;

        for (AbstractElementTransfer transfer : transferTypes) {
            enabled |= transfer.getCopyAndPasteStrategy().canCopy(selection);
        }

        action.setEnabled(enabled);
    }
}
