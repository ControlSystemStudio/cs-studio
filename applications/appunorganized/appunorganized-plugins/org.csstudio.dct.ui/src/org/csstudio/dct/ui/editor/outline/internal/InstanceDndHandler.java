package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.commands.CloneInstanceCommand;
import org.csstudio.dct.model.commands.RemoveInstanceCommand;
import org.csstudio.dct.util.ModelValidationUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * Drag and Drop handler for {@link IInstance}s.
 *
 * @author Sven Wende
 *
 */
public class InstanceDndHandler extends AbstractDnDHandler {

    /**
     *{@inheritDoc}
     */
    @Override
    protected Command doCreateCopyCommand(IElement dndSource, IElement dndTarget) {
        assert dndSource instanceof IInstance;
        IInstance instance = (IInstance) dndSource;

        IFolder targetFolder = dndTarget instanceof IFolder ? (IFolder) dndTarget : null;
        IContainer targetContainer = dndTarget instanceof IContainer ? (IContainer) dndTarget : null;
        assert targetFolder != null || targetContainer != null;

        Command result;

        if(targetFolder!=null) {
            result = new CloneInstanceCommand(instance, targetFolder, "Copy of ");
        } else {
            result = new CloneInstanceCommand(instance, targetContainer, "Copy of ");
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected Command doCreateMoveCommand(IElement dndSource, IElement dndTarget) {
        assert dndSource instanceof IInstance;
        assert (dndTarget instanceof IFolder || dndTarget instanceof IContainer);

        IInstance instance = (IInstance) dndSource;

        // .. chain all command necessary to remove an instance and all
        // dependent instances
        CompoundCommand cmd = new CompoundCommand();

        // .. remove all dependent instances
        List<IInstance> dependentInstances = ModelValidationUtil.recursivelyGetDependentInstances(instance);

        for(IInstance i : dependentInstances) {
            cmd.add(new RemoveInstanceCommand(i));
        }

        // .. remove the instance itself
        cmd.add(new RemoveInstanceCommand(instance));

        // .. chain all commands to add the instance to their final destination
        if (dndTarget instanceof IFolder) {
            // .. move into to a folder
            cmd.add(new AddInstanceCommand((IFolder) dndTarget, instance, false, -1));
        } else if (dndTarget instanceof IContainer) {
            // .. move relative to another container (that might resist in a
            // folder or a container)
            IContainer container = (IContainer) dndTarget;

            IFolder targetFolder = container.getParentFolder();
            IContainer targetContainer = container.getContainer();

            if (targetFolder != null) {
                assert targetContainer == null;
                // .. move within the parent folder
                int index = targetFolder.getMembers().indexOf(container);

                int tmp = targetFolder.getMembers().indexOf(instance);
                if(tmp>-1 && tmp<index) {
                    index--;
                }

                cmd.add(new AddInstanceCommand(targetFolder, instance, false, index));
            } else {
                // .. move within the parent container
                assert targetFolder == null;

                int index =targetContainer.getInstances().indexOf(container);
                int tmp = targetContainer.getInstances().indexOf(instance);
                if(tmp>-1 && tmp<index) {
                    index--;
                }

                cmd.add(new AddInstanceCommand(targetContainer, instance, false, index));
            }

        }

        return cmd;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int updateDragFeedback(IElement dndSource, IElement dndTarget, DropTargetEvent event) {
        IInstance instance = (IInstance) dndSource;

        if (dndSource == dndTarget) {
            // .. its not possible to move or copy to itself
            event.feedback = DND.FEEDBACK_NONE;
        } else if (event.detail == DND.DROP_COPY && dndTarget instanceof IFolder) {
            // .. its always possbible to copy to a folder
            event.feedback = DND.FEEDBACK_SELECT;
        } else if (event.detail == DND.DROP_COPY && dndTarget instanceof IContainer
                && !ModelValidationUtil.causesTransitiveLoop((IContainer) dndTarget, instance.getPrototype())) {
            // .. its possible to copy to containers if not transitive
            // relationships will be caused
            event.feedback = DND.FEEDBACK_SELECT;
        } else if (event.detail == DND.DROP_MOVE && dndTarget instanceof IFolder) {
            // .. its always possible to move to a folder
            event.feedback = DND.FEEDBACK_SELECT;
        } else if (event.detail == DND.DROP_MOVE && dndTarget instanceof IContainer) {
            IContainer container = (IContainer) dndTarget;

            if (container.getParentFolder() != null) {
                assert container.getContainer() == null;
                // .. its always possible to move within a folder
                event.feedback = DND.FEEDBACK_INSERT_BEFORE;
            } else {
                assert container.getParentFolder() == null;
                assert container.getContainer() != null;
                // .. its possible to move within containers when no transitive
                // relationships will be caused
                if (!ModelValidationUtil.causesTransitiveLoop(container.getContainer(), instance.getPrototype())) {
                    event.feedback = DND.FEEDBACK_INSERT_BEFORE;
                } else {
                    event.feedback = DND.FEEDBACK_NONE;
                }
            }

        } else {
            event.feedback = DND.FEEDBACK_NONE;
        }

        return 0;
    }
}
