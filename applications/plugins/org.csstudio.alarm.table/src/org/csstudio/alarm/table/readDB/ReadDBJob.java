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

	private final Calendar _to;
	private final Calendar from;
	private final DBAnswer dbAnswer;
	private final String _filter;
	
	public ReadDBJob(String name, DBAnswer dbAnswer,
			Calendar from, Calendar to) {
		super(name);
		this.dbAnswer = dbAnswer;
		this.from = from;
		this._to = to;
		this._filter = null;
	}

	public ReadDBJob(String name, DBAnswer dbAnswer,
			Calendar from, Calendar to, String filter) {
		super(name);
		this.dbAnswer = dbAnswer;
		this.from = from;
		this._to = to;
		_filter = filter;
	}

	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
        ILogMessageArchiveAccess adba = new ArchiveDBAccess();
        ArrayList<HashMap<String, String>> am = new ArrayList<HashMap<String,String>>();
        if (_filter == null) {
        	am = adba.getLogMessages(from, _to);
        } else {
        	am = adba.getLogMessages(from, _to, _filter);
        }
        dbAnswer.setDBAnswer(am);
		return Status.OK_STATUS;
	}

}
