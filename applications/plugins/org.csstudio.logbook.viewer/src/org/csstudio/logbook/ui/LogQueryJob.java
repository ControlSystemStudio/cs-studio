/**
 * 
 */
package org.csstudio.logbook.ui;

import java.util.ArrayList;
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
		result = new LogResult(logs, null);
	    } catch (Exception e) {
		result = new LogResult(null, e);
	    } finally {
		if (!monitor.isCanceled()) {
		    completedQuery(result);
		}
	    }
	    return Status.OK_STATUS;
	}

}
