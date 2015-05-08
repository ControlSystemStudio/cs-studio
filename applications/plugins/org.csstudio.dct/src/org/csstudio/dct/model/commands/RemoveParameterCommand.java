/**
 *
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Parameter;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that removes a parameter of a {@link IPrototype}.
 *
 * @author Sven Wende
 */
public final class RemoveParameterCommand extends Command {
    private IPrototype prototype;
    private Parameter parameter;

    /**
     * Constructor.
     * @param prototype the prototype
     * @param parameter the parameter
     */
    public RemoveParameterCommand(IPrototype prototype, Parameter parameter) {
        assert prototype != null;
        assert parameter != null;
        this.prototype = prototype;
        this.parameter = parameter;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        prototype.removeParameter(parameter);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        prototype.addParameter(parameter);
    }

}
