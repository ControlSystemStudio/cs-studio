/**
 *
 */
package org.csstudio.sds.model.commands;

import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.gef.commands.Command;

public class ChangePropertyCommand extends Command {
    private WidgetProperty property;
    private Object oldValue;
    private Object newValue;

    public ChangePropertyCommand(WidgetProperty property, Object newValue) {
        assert property != null;
        this.property = property;
        this.newValue = newValue;
    }

    @Override
    public void execute() {
        oldValue = property.getPropertyValue();
        property.setPropertyValue(newValue);
    }

    @Override
    public void undo() {
        property.setPropertyValue(oldValue);
    }

}