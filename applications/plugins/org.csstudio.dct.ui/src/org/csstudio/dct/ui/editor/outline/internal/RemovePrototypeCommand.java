package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command which removes an element from the model.
 * 
 * @author Sven Wende
 * 
 */
public class RemovePrototypeCommand extends Command implements ISelectAfterExecution {
	private IPrototype prototype;
	private IFolder folder;
	
	/**
	 * Constructor.
	 * 
	 * @param folder
	 *            the folder which will contain the new prototype
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
		folder.removeMember(prototype);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		folder.addMember(prototype);
	}

	/**
	 * {@inheritDoc}
	 */
	public IElement getElementToSelect() {
		return null;
	}

}
