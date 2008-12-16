/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Parameter;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that adds a parameter to a {@link IPrototype}.
 * 
 * @author Sven Wende
 */
public class AddParameterCommand extends Command {
	private IPrototype prototype;
	private Parameter parameter;

	public AddParameterCommand(IPrototype prototype, Parameter parameter) {
		assert prototype != null;
		assert parameter != null;
		assert prototype.hasParameter(parameter.getName());
		this.prototype = prototype;
		this.parameter = parameter;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		prototype.addParameter(parameter);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		prototype.removeParameter(parameter);
	}

}