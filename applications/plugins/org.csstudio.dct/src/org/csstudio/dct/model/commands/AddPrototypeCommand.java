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
public class AddPrototypeCommand extends Command {
	private IFolder folder;
	private IPrototype prototype;

	public AddPrototypeCommand(IFolder folder, IPrototype prototype) {
		assert folder != null;
		assert prototype != null;
		assert prototype.getParentFolder() == null;
		assert prototype.getParent() == null;
		this.folder = folder;
		this.prototype = prototype;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		folder.addMember(prototype);
		prototype.setParentFolder(folder);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		folder.removeMember(prototype);
		prototype.setParentFolder(null);
	}

}
