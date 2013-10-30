package org.csstudio.logbook.olog.properties;

import static org.csstudio.logbook.PropertyBuilder.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.PropertyBuilder;
import org.csstudio.security.SecuritySupport;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 */

/**
 * @author shroffk
 * 
 */
public class ReviewSign extends AbstractHandler {

    public static final String ID = "org.csstudio.logbook.olog.properties.reviewsign";

    private LogbookClient logbookClient;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	ISelection selection = HandlerUtil.getActiveMenuSelection(event);
	List<LogEntryBuilder> data = Arrays.asList(AdapterUtil.convert(
		selection, LogEntryBuilder.class));
	if (data == null || data.isEmpty()) {
	    ErrorDialog.openError(HandlerUtil.getActiveWorkbenchWindow(event)
		    .getShell(), "Error",
		    "No log Entries selected to be signed", new Status(
			    IStatus.ERROR, ID, null));
	} else {
	    try {
		logbookClient = LogbookClientManager.getLogbookClientFactory().getClient("shroffk", "1234");

		String signature = SecuritySupport
			.getSubjectName(SecuritySupport.getSubject());
		PropertyBuilder SignOffProperty = property("SignOff")
			.attribute("signature", signature);
		StringBuffer sb = new StringBuffer();
		sb.append("signature:" + signature + System.getProperty("line.separator"));

		Boolean result = MessageDialog
			.openConfirm(
				HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
				"Confirm the review and the sign off the entires:" + data.size() +" logEntries",
				sb.toString());
		if(result){
		    for (LogEntryBuilder logEntryBuilder : data) {
			logEntryBuilder.addProperty(SignOffProperty);
			sb.append("logEntry: " + logEntryBuilder + System.getProperty("line.separator"));
			logbookClient.updateLogEntry(logEntryBuilder.build());
		    }
		}
	    } catch (Exception e1) {		
		e1.printStackTrace();
	    }

	}
	return event;
    }

}
