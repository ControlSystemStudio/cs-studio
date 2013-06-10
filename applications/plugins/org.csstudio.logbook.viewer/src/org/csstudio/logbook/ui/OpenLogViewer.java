/**
 * 
 */
package org.csstudio.logbook.ui;

import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author shroffk
 * 
 */
public class OpenLogViewer extends AbstractAdaptedHandler<LogEntry> {

    public OpenLogViewer() {
	super(LogEntry.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(List<LogEntry> data, ExecutionEvent event)
	    throws Exception {

	if (data == null || data.isEmpty()) {
	    // Get data from command event
	    Object trigger = event.getTrigger();
	    if (trigger instanceof Event) {
		Object eventData = ((Event) trigger).data;
		if (eventData instanceof List) {
		    data = (List<LogEntry>) eventData;
		}
	    }
	}
	if (data == null || data.isEmpty()) {
	    LogViewer.createInstance();
	} else if (data.size() == 1) {
	    LogViewer
		    .createInstance(new LogViewerModel(data.iterator().next()));
	} else {
	    // Throw exception
	}
	try {
	    final IWorkbench workbench = PlatformUI.getWorkbench();
	    final IWorkbenchWindow window = workbench
		    .getActiveWorkbenchWindow();
	    workbench.showPerspective(LogViewerPerspective.ID, window);
	} catch (Exception ex) {
	    // never mind
	}
    }

}
