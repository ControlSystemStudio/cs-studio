/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IInstance;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes a parameter value of an {@link IInstance}.
 * 
 * @author Sven Wende
 */
public class ChangeParameterValueCommand extends Command {
	private IInstance instance;
	private String key;
	private String value;
	private String oldValue;

	public ChangeParameterValueCommand(IInstance container, String key, String value) {
		assert container != null;
		assert key != null;
		assert value != null;
		this.instance = container;
		this.key = key;
		this.value = value;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		oldValue = instance.getParameterValue(key);
		
		Object parentValue = instance.getParent() != null ? instance.getParent().getParameterValue(key) : "";
		
		if (value == null || "".equals(value) || value.equals(parentValue)) {
			instance.setParameterValue(key, null);
		} else {
			instance.setParameterValue(key, value);
		}
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		if (oldValue != null) {
			instance.setParameterValue(key, oldValue);
		} else {
			instance.setParameterValue(key, null);
		}
	}

}