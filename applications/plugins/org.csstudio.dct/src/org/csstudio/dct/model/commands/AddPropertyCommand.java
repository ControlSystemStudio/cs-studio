/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IPropertyContainer;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes the key of a property of a
 * {@link IPropertyContainer}.
 * 
 * @author Sven Wende
 */
public class AddPropertyCommand extends Command {
	private IPropertyContainer container;
	private String key;
	private String newKey;

	public AddPropertyCommand(IPropertyContainer container, String key) {
		assert container != null;
		assert key != null;
		this.container = container;
		this.key = key;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		container.addProperty(key, "");
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		container.removeProperty(key);
	}

}