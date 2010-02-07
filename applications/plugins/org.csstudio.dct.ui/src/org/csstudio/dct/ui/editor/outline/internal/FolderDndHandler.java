package org.csstudio.dct.ui.editor.outline.internal;

import java.util.Set;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.RemovePrototypeCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

public class FolderDndHandler extends AbstractDnDHandler {

	@Override
	protected Command doCreateCopyCommand(IElement source, IElement target) {
		return null;
	}

	@Override
	protected Command doCreateMoveCommand(IElement source, IElement target) {
		IPrototype prototype = (IPrototype) source;

		CompoundCommand cmd = new CompoundCommand();

		IContainer targetContainer = (IContainer) target;

		cmd.add(new RemovePrototypeCommand(prototype));
		cmd.add(new AddPrototypeCommand(targetContainer.getParentFolder(), prototype));

		return cmd;
	}

	@Override
	public int updateDragFeedback(IElement dndSource, IElement dndTarget, DropTargetEvent event) {
		if (event.detail == DND.DROP_COPY || event.detail == DND.DROP_MOVE) {
			if (dndTarget instanceof IFolder) {
				event.feedback = DND.FEEDBACK_INSERT_AFTER;
			} else if (dndTarget instanceof IContainer) {
				IContainer container = (IContainer) dndTarget;
				if(container.getParentFolder()!=null) {
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
