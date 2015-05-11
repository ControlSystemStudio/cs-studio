package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command which removes an prototype from the model.
 *
 * @author Sven Wende
 *
 */
public final class RemovePrototypeCommand extends Command {
    private IPrototype prototype;
    private IFolder folder;
    private int index;

    /**
     * Constructor.
     *
     * @param prototype
     *            the prototype
     */
    public RemovePrototypeCommand(IPrototype prototype) {
        assert prototype != null;
        this.prototype = prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        folder = prototype.getParentFolder();
        index = folder.getMembers().indexOf(prototype);
        folder.removeMember(prototype);
        prototype.setParentFolder(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        folder.addMember(Math.min(index, folder.getMembers().size()), prototype);
        prototype.setParentFolder(folder);
    }
}
