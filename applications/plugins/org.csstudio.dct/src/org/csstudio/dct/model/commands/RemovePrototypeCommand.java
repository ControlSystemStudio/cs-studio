package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command which removes an prototype from the model.
 * 
 * @author Sven Wende
 * 
 */
public final class RemovePrototypeCommand extends Command implements ISelectAfterExecution {
	private IPrototype prototype;
	private IFolder folder;

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
