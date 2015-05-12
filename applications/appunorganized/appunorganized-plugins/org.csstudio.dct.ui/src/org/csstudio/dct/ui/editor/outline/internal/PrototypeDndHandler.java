package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.ClonePrototypeCommand;
import org.csstudio.dct.model.commands.RemovePrototypeCommand;
import org.csstudio.dct.model.internal.Prototype;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * Drag and Drop handler for {@link Prototype}s.
 *
 * @author Sven Wende
 *
 */
public class PrototypeDndHandler extends AbstractDnDHandler {

    @Override
    protected Command doCreateCopyCommand(IElement dndSource, IElement dndTarget) {
        assert dndSource instanceof IPrototype;
        assert dndTarget instanceof IFolder;

        IPrototype prototype = (IPrototype) dndSource;
        IFolder folder = (IFolder) dndTarget;

        return new ClonePrototypeCommand(prototype, folder, "Copy of");

    }

    @Override
    protected Command doCreateMoveCommand(IElement dndSource, IElement dndTarget) {
        assert dndSource instanceof IPrototype;
        assert (dndTarget instanceof IFolder || (dndTarget instanceof IContainer && ((IContainer) dndTarget).getParentFolder() != null));

        IPrototype prototype = (IPrototype) dndSource;

        // .. determine folder and insertation index
        int index = 0;
        IFolder folder = null;

        if (dndTarget instanceof IFolder) {
            folder = (IFolder) dndTarget;
        } else {
            folder = ((IContainer) dndTarget).getParentFolder();

            index = folder.getMembers().indexOf(dndTarget);
            int tmp = folder.getMembers().indexOf(prototype);
            if(tmp>-1 && tmp<index) {
                index--;
            }
        }

        assert folder != null;

        // .. create command
        CompoundCommand cmd = new CompoundCommand();
        cmd.add(new RemovePrototypeCommand(prototype));
        cmd.add(new AddPrototypeCommand(folder, prototype, index));
        return cmd;
    }

    @Override
    public int updateDragFeedback(IElement dndSource, IElement dndTarget, DropTargetEvent event) {
        if (dndSource == dndTarget) {
            event.feedback = DND.FEEDBACK_NONE;
        } else if (event.detail == DND.DROP_COPY) {
            if (dndTarget instanceof IFolder) {
                event.feedback = DND.FEEDBACK_SELECT;
            } else {
                event.feedback = DND.FEEDBACK_NONE;
            }
        } else if (event.detail == DND.DROP_MOVE) {
            if (dndTarget instanceof IFolder) {
                event.feedback = DND.FEEDBACK_SELECT;
            } else if (dndTarget instanceof IContainer) {
                IContainer container = (IContainer) dndTarget;
                if (container.getParentFolder() != null) {
                    event.feedback = DND.FEEDBACK_INSERT_BEFORE;
                } else {
                    event.feedback = DND.FEEDBACK_NONE;
                }
            } else {
                event.feedback = DND.FEEDBACK_NONE;
            }
        } else {
            event.feedback = DND.FEEDBACK_NONE;
        }

        return 0;
    }

}
