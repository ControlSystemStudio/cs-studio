/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IElement;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes the name of an {@link IElement}.
 * 
 * @author Sven Wende
 * 
 */
public class ChangeNameCommand extends Command {

	private IElement element;
	private String name;
	private String oldName;

	public ChangeNameCommand(IElement element, String name) {
		this.element = element;
		this.name = name;
		this.oldName = element.getName();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		element.setName(name);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		element.setName(oldName);
	}
}