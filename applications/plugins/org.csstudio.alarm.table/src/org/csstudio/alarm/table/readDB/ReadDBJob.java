/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.alarm.table.readDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.FilterItem;
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
	private ArrayList<FilterItem> _filterSettings;
	
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
			Calendar from, Calendar to, String filter, ArrayList<FilterItem> filterSettings) {
		super(name);
		this.dbAnswer = dbAnswer;
		this.from = from;
		this._to = to;
		_filter = filter;
		_filterSettings = filterSettings;
		String maxAnswerSize = JmsLogsPlugin.getDefault().getPluginPreferences().getString("maximum answer size");
		_maxAnswerSize = Integer.parseInt(maxAnswerSize);
	}

	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
        ILogMessageArchiveAccess adba = ArchiveDBAccess.getInstance();
        ArrayList<HashMap<String, String>> am = new ArrayList<HashMap<String,String>>();
        if (_filter == null) {
        	am = adba.getLogMessages(from, _to, _maxAnswerSize);
        } else {
        	am = adba.getLogMessages(from, _to, _filter, _filterSettings, _maxAnswerSize);
        }
        dbAnswer.setDBAnswer(am);
		return Status.OK_STATUS;
	}

}
