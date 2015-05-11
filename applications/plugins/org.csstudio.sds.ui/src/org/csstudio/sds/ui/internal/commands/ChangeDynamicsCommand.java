/**
 *
 */
package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.gef.commands.Command;

/**
 * Command that changes the {@link DynamicsDescriptor} of a
 * {@link WidgetProperty}.
 *
 * @author Sven Wende
 *
 */
public class ChangeDynamicsCommand extends Command {
    private WidgetProperty property;
    private DynamicsDescriptor oldValue;
    private DynamicsDescriptor newValue;

    public ChangeDynamicsCommand(WidgetProperty property, DynamicsDescriptor newValue) {
        assert property != null;
        this.property = property;
        this.newValue = newValue;
    }

    @Override
    public void execute() {
        oldValue = property.getDynamicsDescriptor();
        property.setDynamicsDescriptor(newValue);
    }

    @Override
    public void undo() {
        property.setDynamicsDescriptor(oldValue);
    }

}