package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.dnd.DropTargetEvent;

public abstract class AbstractDnDHandler<E extends IElement> {
    public abstract int updateDragFeedback(E dndSource, IElement dndTarget, DropTargetEvent event);

    public final Command createCopyCommand(E dndSource, IElement dndTarget) {
        return doCreateCopyCommand(dndSource, dndTarget);
    }

    public final Command createMoveCommand(E dndSource, IElement dndTarget) {
        return doCreateMoveCommand(dndSource, dndTarget);
    }

    protected abstract Command doCreateCopyCommand(E dndSource, IElement dndTarget);
    protected abstract Command doCreateMoveCommand(E dndSource, IElement dndTarget);

    public boolean supports(E dndSource) {
        return true;
    }

}

