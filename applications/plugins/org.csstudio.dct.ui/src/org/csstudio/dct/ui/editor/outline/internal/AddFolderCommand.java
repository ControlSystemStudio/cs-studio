package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Undoable command which adds a new record to the model.
 * 
 * @author Sven Wende
 * 
 */
public class AddFolderCommand extends Command implements ISelectAfterExecution {
	private IFolder folder;
	private IFolder parentFolder;

	/**
	 * Constructor.
	 * 
	 * @param parentFolder
	 *            the folder which will contain the new prototype
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
