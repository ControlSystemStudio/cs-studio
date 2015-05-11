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
public final class RemovePropertyCommand extends Command {
    private IPropertyContainer container;
    private String key;
    private String value;

    /**
     * Constructor.
     * @param container the property container
     * @param key the key
     */
    public RemovePropertyCommand(IPropertyContainer container, String key) {
        assert container != null;
        assert key != null;
        assert container.hasProperty(key);
        this.container = container;
        this.key = key;
        this.value = container.getProperty(key);

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        container.removeProperty(key);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        container.addProperty(key, value);
    }

}
