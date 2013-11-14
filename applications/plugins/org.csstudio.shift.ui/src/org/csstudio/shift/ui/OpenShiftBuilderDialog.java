package org.csstudio.shift.ui;

import java.util.List;

import org.csstudio.shift.ShiftBuilder;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenShiftBuilderDialog extends AbstractAdaptedHandler<ShiftBuilder> {

    public OpenShiftBuilderDialog() {
	    super(ShiftBuilder.class);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void execute(List<ShiftBuilder> data, final ExecutionEvent event) throws Exception {
        final Shell shell = HandlerUtil.getActiveShell(event);
        if (data == null || data.isEmpty()) {
            // Get data from command event
            final Object trigger = event.getTrigger();
            if(trigger instanceof Event) {
                final Object eventData = ((Event)trigger).data;
                if(eventData instanceof List) {
                    data = (List<ShiftBuilder>) eventData;
                }
            }
        }
        if (data == null || data.isEmpty()) {
            final ShiftBuilderDialog dialog = new ShiftBuilderDialog(shell, ShiftBuilder.withType(""));
            dialog.setBlockOnOpen(true);
            if (dialog.open() == Window.OK) {
            }
        } else if (data.size() == 1) {
            final ShiftBuilderDialog dialog = new ShiftBuilderDialog(shell, data.iterator().next());
            dialog.setBlockOnOpen(true);
            if (dialog.open() == Window.OK) {
            }
        } else {
            // Throw exception
        }
    }
}
