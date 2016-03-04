package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command the removes an {@link IInstance} from a {@link IFolder} or a
 * {@link IContainer}.
 *
 * @author Sven Wende
 *
 */
public final class RemoveInstanceCommand extends Command {
    private IContainer container;
    private IFolder folder;
    private IInstance instance;
    private IContainer parent;
    private int index;

    /**
     * Constructor.
     * @param instance the instance
     */
    public RemoveInstanceCommand(IInstance instance) {
        assert instance != null;
        assert instance.getParentFolder() != null || instance.getContainer() != null;

        this.instance = instance;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        this.folder = instance.getParentFolder();
        this.container = (IContainer) instance.getContainer();


        if (folder != null) {
            index = folder.getMembers().indexOf(instance);
            folder.removeMember(instance);
            instance.setParentFolder(null);
        } else if(container!=null) {
            index = container.getInstances().indexOf(instance);
            container.removeInstance(instance);
            instance.setContainer(null);
        }

        // ... unlink from super
        parent = instance.getParent();

        if(parent!=null) {
            parent.removeDependentContainer(instance);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        if (folder != null) {
            folder.addMember(Math.min(index, folder.getMembers().size()), instance);
            instance.setParentFolder(folder);
        } else if (container !=null){
            container.addInstance(Math.min(index, container.getInstances().size()), instance);
            instance.setContainer(container);
        }

        // ... link to super
        if(parent!=null) {
            parent.addDependentContainer(instance);
        }
    }

}
