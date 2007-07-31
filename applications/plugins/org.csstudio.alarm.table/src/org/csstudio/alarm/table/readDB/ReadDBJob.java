package org.csstudio.alarm.table.readDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Job for accessing the database
 * 
 * @author jhatje
 *
 */

public class ReadDBJob extends Job {

	private final Calendar to;
	private final Calendar from;
	private final DBAnswer dbAnswer;

	public ReadDBJob(String name, DBAnswer dbAnswer,
			Calendar from, Calendar to) {
		super(name);
		this.dbAnswer = dbAnswer;
		this.from = from;
		this.to = to;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
        ILogMessageArchiveAccess adba = new ArchiveDBAccess();
        ArrayList<HashMap<String, String>> am = adba.getLogMessages(
                from, to);
        dbAnswer.setDBAnswer(am);
		return Status.OK_STATUS;
	}

}
