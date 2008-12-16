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
public class ChangePropertyKeyCommand extends Command {
	private IPropertyContainer container;
	private String key;
	private String value;
	private String newKey;

	public ChangePropertyKeyCommand(IPropertyContainer container, String key, String newKey) {
		assert container != null;
		assert key != null;
		assert value != null;
		this.container = container;
		this.key = key;
		this.newKey = newKey;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		doSwitch(key, newKey);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		doSwitch(newKey, key);
	}

	private void doSwitch(String key, String newKey) {
		String value = container.getProperty(key);
		container.removeProperty(key);
		container.addProperty(newKey, value);

		System.err.println("Switched from :" + key + " to " + newKey + " with value " + value);
	}

}