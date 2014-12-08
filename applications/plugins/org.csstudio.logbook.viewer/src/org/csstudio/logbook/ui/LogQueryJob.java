/**
 * 
 */
package org.csstudio.logbook.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.ui.PeriodicLogQuery.LogResult;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Kunal Shroff
 * 
 */
public class LogQueryJob extends Job {

    private final static String name = "LogQueryJob";

    private final String query;
    private final LogbookClient logbookClient;

    LogQueryJob(String query, LogbookClient logbookClient) {
	super(name);
	this.query = query;
	this.logbookClient = logbookClient;
    }

    void completedQuery(LogResult result) {

    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
	LogResult result = null;
	try {
	    List<LogEntry> logs = new ArrayList<LogEntry>(
		    logbookClient.findLogEntries(query));
	    Collections.sort(logs, new Comparator<LogEntry>() {

		@Override
		public int compare(LogEntry o1, LogEntry o2) {
		    Date d1 = o1.getCreateDate();
		    Date d2 = o2.getCreateDate();
		    return d2.compareTo(d1);
		}

	    });
	    result = new LogResult(logs, null);
	} catch (Exception e) {
	    result = new LogResult(new ArrayList<LogEntry>(0), e);
	} finally {
	    if (!monitor.isCanceled()) {
		completedQuery(result);
	    }
	}
	return Status.OK_STATUS;
    }

}
