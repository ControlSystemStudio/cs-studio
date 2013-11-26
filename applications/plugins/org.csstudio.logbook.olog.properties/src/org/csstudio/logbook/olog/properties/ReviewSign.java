package org.csstudio.logbook.olog.properties;

import java.util.Arrays;
import java.util.List;

import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 * 
 */
public class ReviewSign extends AbstractHandler {

    public static final String ID = "org.csstudio.logbook.olog.properties.reviewsign";

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
	ISelection selection = HandlerUtil.getActiveMenuSelection(event);
	final List<LogEntryBuilder> data = Arrays.asList(AdapterUtil.convert(
		selection, LogEntryBuilder.class));
	if (data == null || data.isEmpty()) {
	    ErrorDialog.openError(HandlerUtil.getActiveWorkbenchWindow(event)
		    .getShell(), "Error",
		    "No log Entries selected to be signed", new Status(
			    IStatus.ERROR, ID, null));
	} else {
	    try {

		Runnable openSearchDialog = new Runnable() {
		    @Override
		    public void run() {
			try {
			    Display.getDefault().asyncExec(new Runnable() {
				public void run() {
				    ReviewSignDialog dialog = new ReviewSignDialog(
					    HandlerUtil.getActiveWorkbenchWindow(event)
					    .getShell(), data);
				    dialog.setBlockOnOpen(true);
				    if (dialog.open() == IDialogConstants.OK_ID) {

				    }
				}
			    });
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		};
		BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
	    } catch (Exception e1) {
		e1.printStackTrace();
	    }

	}
	return event;
    }

}
