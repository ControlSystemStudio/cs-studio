package org.csstudio.shift.ui;

import gov.bnl.shiftClient.ShiftClient;

import java.util.List;

import org.csstudio.shift.ShiftBuilder;
import org.csstudio.shift.ShiftClientManager;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class CloseShiftBuilder extends AbstractAdaptedHandler<ShiftBuilder> {

    public CloseShiftBuilder() {
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
        if (data != null && data.size() == 1) {
        	ShiftBuilder shift = data.iterator().next();
        	final ShiftClient shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
        	shift = ShiftBuilder.shift(shiftClient.getShift(shift.build().getId(), shift.build().getType().getName()));
        	if(shift.build().getEndDate() != null && shift.build().getCloseShiftUser() == null) {
        	
	        	final CloseShiftBuilderDialog dialog = new CloseShiftBuilderDialog(shell, shift, false);
	            dialog.setBlockOnOpen(true);
	            if (dialog.open() == Window.OK) {
	            }
        	}
        } else {
            // Throw exception
        }  
    }
}
