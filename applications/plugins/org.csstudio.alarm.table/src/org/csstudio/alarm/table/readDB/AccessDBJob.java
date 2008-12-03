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

public class AccessDBJob extends Job {

	private Calendar _to;
	private Calendar _from;
	private DBAnswer dbAnswer = null;
	private int _maxAnswerSize;
	private ArrayList<FilterItem> _filterSettings;
	private boolean _deleteMessages = false;

	public AccessDBJob(String name) {
		super(name);
	}

	public void setReadProperties(DBAnswer dbAnswer, Calendar from, Calendar to) {
		setReadProperties(dbAnswer, from, to, null);
	}

	public void setReadProperties(DBAnswer dbAnswer, Calendar from,
			Calendar to, ArrayList<FilterItem> filterSettings) {
		this._deleteMessages = false;
		this.dbAnswer = dbAnswer;
		this._from = from;
		this._to = to;
		this._filterSettings = filterSettings;
		String maxAnswerSize = JmsLogsPlugin.getDefault()
				.getPluginPreferences().getString("maximum answer size");
		_maxAnswerSize = Integer.parseInt(maxAnswerSize);
	}

	public void setDeleteFlag(boolean deleteMessages) {
		this._deleteMessages = deleteMessages;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		ILogMessageArchiveAccess adba = ArchiveDBAccess.getInstance();
		ArrayList<HashMap<String, String>> am = new ArrayList<HashMap<String, String>>();
		if (_deleteMessages == false) {
			am = adba.getLogMessages(_from, _to, _filterSettings, _maxAnswerSize);
		} else {
			am = adba.deleteLogMessages(_from, _to, _filterSettings);
		}
		_deleteMessages = false;
		dbAnswer.setDBAnswer(am, adba.is_maxSize());
		return Status.OK_STATUS;
	}

}
