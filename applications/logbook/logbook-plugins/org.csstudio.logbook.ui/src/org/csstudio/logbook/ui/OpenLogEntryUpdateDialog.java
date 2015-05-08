/**
 *
 */
package org.csstudio.logbook.ui;

import java.util.List;

import org.csstudio.logbook.ui.util.UpdateLogEntryBuilder;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 *
 */
public class OpenLogEntryUpdateDialog extends AbstractAdaptedHandler<UpdateLogEntryBuilder> {

    public OpenLogEntryUpdateDialog() {
    super(UpdateLogEntryBuilder.class);
    }

    @Override
    protected void execute(List<UpdateLogEntryBuilder> data, ExecutionEvent event)
        throws Exception {
    final Shell shell = HandlerUtil.getActiveShell(event);
        if (data != null && !data.isEmpty()) {
            if (data.size() == 1) {
                LogEntryUpdateDialog dialog = new LogEntryUpdateDialog(shell,
                        data.iterator().next().getLogEntryBuilder().build());
                dialog.setBlockOnOpen(true);
                if (dialog.open() == Window.OK) {
                }
            }
        } else {
            // Throw exception
        }
    }
}

