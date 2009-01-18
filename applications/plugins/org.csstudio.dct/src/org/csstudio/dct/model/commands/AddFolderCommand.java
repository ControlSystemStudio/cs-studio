package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.internal.Folder;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command which adds a new folder to the model.
 * 
 * @author Sven Wende
 * 
 */
public final class AddFolderCommand extends Command implements ISelectAfterExecution {
	private IFolder folder;
	private IFolder parentFolder;

	/**
	 * Constructor.
	 * 
	 * @param parentFolder
	 *            the parent folder
	 * @param name
	 *            then name of the new folder
	 */
	public AddFolderCommand(IFolder parentFolder, String name) {
		assert parentFolder != null;
		assert name != null;
		this.parentFolder = parentFolder;
		this.folder = new Folder(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		parentFolder.addMember(folder);
		folder.setParentFolder(parentFolder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		parentFolder.removeMember(folder);
		folder.setParentFolder(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public IElement getElementToSelect() {
		return folder;
	}

}
