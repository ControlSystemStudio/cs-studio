/**
 *
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IPropertyContainer;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that adds a property.
 *
 * @author Sven Wende
 */
public final class AddPropertyCommand extends Command {
    private IPropertyContainer container;
    private String key;
    /**
     * Constructor.
     *
     * @param container
     *            the property container
     * @param key
     *            the property key
     */
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
