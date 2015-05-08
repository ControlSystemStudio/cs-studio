/**
 *
 */
package org.csstudio.logbook.ui;

import java.util.List;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 *
 */
public class OpenLogEntryBuilderDialog extends
    AbstractAdaptedHandler<LogEntryBuilder> {

    public OpenLogEntryBuilderDialog() {
    super(LogEntryBuilder.class);
    }

    @Override
    protected void execute(List<LogEntryBuilder> data, ExecutionEvent event)
        throws Exception {
    final Shell shell = HandlerUtil.getActiveShell(event);
    if (data == null || data.isEmpty()) {
        // Get data from command event
        Object trigger = event.getTrigger();
        if(trigger instanceof Event) {
            Object eventData = ((Event)trigger).data;
            if(eventData instanceof List) {
                data = (List<LogEntryBuilder>) eventData;
            }
        }
    }
    if (data == null || data.isEmpty()) {
        LogEntryBuilderDialog dialog = new LogEntryBuilderDialog(null,
            LogEntryBuilder.withText(""));
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
        }
    } else if (data.size() == 1) {
        LogEntryBuilderDialog dialog = new LogEntryBuilderDialog(null,
            data.iterator().next());
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
        }
    } else {
        // Throw exception
    }
    }
}

