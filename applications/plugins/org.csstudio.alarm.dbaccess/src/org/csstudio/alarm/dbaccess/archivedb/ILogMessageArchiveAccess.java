package org.csstudio.alarm.dbaccess.archivedb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public interface ILogMessageArchiveAccess {

	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, int maxAnswerSize);
	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from, Calendar to, String filter, int maxAnswerSize);
}
