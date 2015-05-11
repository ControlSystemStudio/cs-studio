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
public final class ChangePropertyKeyCommand extends Command {
    private IPropertyContainer container;
    private String key;
    private String newKey;

    /**
     * Constructor.
     * @param container the property container
     * @param key the key
     * @param newKey the new key
     */
    public ChangePropertyKeyCommand(IPropertyContainer container, String key, String newKey) {
        assert container != null;
        assert key != null;
        assert newKey != null;
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
        String v = container.getProperty(key);
        container.removeProperty(key);
        container.addProperty(newKey, v);
    }

}
