package org.csstudio.alarm.dbaccess.archivedb;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

public interface ILogMessageArchiveAccess {

	public ArrayList<HashMap<String, String>> getLogMessages(GregorianCalendar from, GregorianCalendar to);
	
}
