package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that adds a {@link IPrototype} to a {@link IFolder}.
 *
 * @author Sven Wende
 *
 */
public final class AddPrototypeCommand extends Command {
    private int index = -1;
    private IFolder folder;
    private IPrototype prototype;

    /**
     * Constructor.
     *
     * @param folder
     *            the folder
     * @param prototype
     *            the prototype
     */
    public AddPrototypeCommand(IFolder folder, IPrototype prototype, int index) {
        this(folder, prototype);
        this.index = index;
    }

    /**
     * Constructor.
     *
     * @param folder
     *            the folder
     * @param prototype
     *            the prototype
     */
    public AddPrototypeCommand(IFolder folder, IPrototype prototype) {
        assert folder != null;
        assert prototype != null;
        this.folder = folder;
        this.prototype = prototype;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        assert prototype.getParentFolder() == null;
        assert prototype.getParent() == null;

        if (index > -1) {
            folder.addMember(Math.min(index, folder.getMembers().size()), prototype);
        } else {
            folder.addMember(prototype);
        }
        prototype.setParentFolder(folder);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        index = folder.getMembers().indexOf(prototype);
        folder.removeMember(prototype);
        prototype.setParentFolder(null);
    }

}
