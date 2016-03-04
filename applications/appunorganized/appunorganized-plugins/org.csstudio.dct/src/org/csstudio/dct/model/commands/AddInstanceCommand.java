package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.internal.Instance;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Command that adds an instance to a folder or container.
 *
 * @author Sven Wende
 *
 */
public final class AddInstanceCommand extends Command {
    private boolean processInitialization;
    private CompoundCommand internalCommand;
    private IContainer container;
    private IFolder folder;
    private IInstance instance;
    private int index;

    /**
     * Constructor.
     *
     * @param folder
     *            the folder
     * @param instance
     *            the instance
     * @param processInitialization
     *            true, if the provided instance needs to be initialized from
     *            its prototype
     * @param index
     *            the insertation index
     */
    public AddInstanceCommand(IFolder folder, IInstance instance, boolean processInitialization, int index) {
        assert instance != null;
        this.instance = instance;
        this.folder = folder;
        this.processInitialization = processInitialization;
        this.index = index;
    }

    /**
     * Constructor.
     *
     * @param folder
     *            the folder
     * @param instance
     *            the instance
     */
    public AddInstanceCommand(IFolder folder, IInstance instance) {
        this(folder, instance, true, -1);
    }

    /**
     * Constructor.
     *
     * @param container
     *            the container
     * @param instance
     *            the instance
     * @param processInitialization
     *            true, if the provided instance needs to be initialized from
     *            its prototype
     * @param index
     *            the insertation index
     */
    public AddInstanceCommand(IContainer container, IInstance instance, boolean processInitialization, int index) {
        assert instance != null;
        this.instance = instance;
        this.container = container;
        this.processInitialization = processInitialization;
        this.index = index;
    }

    /**
     * Constructor.
     *
     * @param container
     *            the container
     * @param instance
     *            the instance
     */
    public AddInstanceCommand(IContainer container, IInstance instance) {
        this(container, instance, true, -1);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        assert instance.getParentFolder() == null;
        assert instance.getContainer() == null;

        internalCommand = new CompoundCommand();

        if (processInitialization) {
            internalCommand.add(new InitInstanceCommand(instance));
        }

        if (folder != null) {
            if(index>-1) {
                folder.addMember(index, instance);
            } else {
                folder.addMember(instance);
            }
            instance.setParentFolder(folder);
        } else {
            if(index>-1) {
                container.addInstance(index, instance);
            } else {
                container.addInstance(instance);
            }

            // ... link physical container
            instance.setContainer(container);

            // ... add-push to model elements that inherit from here
            for (IContainer c : container.getDependentContainers()) {
                Instance pushedInstance = new Instance(instance, UUID.randomUUID());
                internalCommand.add(new AddInstanceCommand(c, pushedInstance, true, index));
            }
        }

        // ... link to super
        instance.getParent().addDependentContainer(instance);

        internalCommand.execute();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        internalCommand.undo();
        if (folder != null) {
            folder.removeMember(instance);
            instance.setParentFolder(null);
        } else {
            container.removeInstance(instance);
            instance.setContainer(null);
        }
        instance.getParent().removeDependentContainer(instance);
    }

}
