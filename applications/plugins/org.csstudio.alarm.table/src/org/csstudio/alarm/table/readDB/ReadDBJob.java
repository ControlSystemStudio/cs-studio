package org.csstudio.alarm.table.readDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.table.JmsLogsPlugin;
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
	private final int _maxAnswerSize;
	
	public ReadDBJob(String name, DBAnswer dbAnswer,
			Calendar from, Calendar to) {
		super(name);
		this.dbAnswer = dbAnswer;
		this.from = from;
		this._to = to;
		this._filter = null;
		String maxAnswerSize = JmsLogsPlugin.getDefault().getPluginPreferences().getString("maximum answer size");
		_maxAnswerSize = Integer.parseInt(maxAnswerSize);
	}

	public ReadDBJob(String name, DBAnswer dbAnswer,
			Calendar from, Calendar to, String filter) {
		super(name);
		this.dbAnswer = dbAnswer;
		this.from = from;
		this._to = to;
		_filter = filter;
		String maxAnswerSize = JmsLogsPlugin.getDefault().getPluginPreferences().getString("maximum answer size");
		_maxAnswerSize = Integer.parseInt(maxAnswerSize);
	}

	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
        ILogMessageArchiveAccess adba = new ArchiveDBAccess();
        ArrayList<HashMap<String, String>> am = new ArrayList<HashMap<String,String>>();
        if (_filter == null) {
        	am = adba.getLogMessages(from, _to, _maxAnswerSize);
        } else {
        	am = adba.getLogMessages(from, _to, _filter, _maxAnswerSize);
        }
        dbAnswer.setDBAnswer(am);
		return Status.OK_STATUS;
	}

}
