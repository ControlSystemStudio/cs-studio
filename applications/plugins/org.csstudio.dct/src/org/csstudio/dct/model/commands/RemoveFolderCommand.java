package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IFolder;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command which removes a folder from the model.
 *
 * @author Sven Wende
 *
 */
public final class RemoveFolderCommand extends Command {
    private IFolder folder;
    private IFolder container;
    private int index;


    /**
     * Constructor.
     *
     * @param folder
     *            the folder which will contain the new prototype
     */
    public RemoveFolderCommand(IFolder folder) {
        assert folder != null;
        this.folder = folder;
        this.container = folder.getParentFolder();
        this.index = container.getMembers().indexOf(folder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        container.removeMember(folder);
        folder.setParentFolder(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        container.addMember(Math.min(index, container.getMembers().size()), folder);
        folder.setParentFolder(container);
    }
}
