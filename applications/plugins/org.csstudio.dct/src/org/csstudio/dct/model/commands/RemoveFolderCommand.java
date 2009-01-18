package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command which removes a folder from the model.
 * 
 * @author Sven Wende
 * 
 */
public final class RemoveFolderCommand extends Command implements ISelectAfterExecution {
	private IFolder folder;
	private IFolder container;

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		container.removeMember(folder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		container.addMember(folder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IElement getElementToSelect() {
		return folder;
	}

}
