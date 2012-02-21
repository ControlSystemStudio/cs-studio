package org.csstudio.archive.common.guard;

import java.util.Calendar;
import java.util.Date;

public class Settings {
	
//krykpcp settings
//	public static final String DATABASE = "archiveengine_test";
//	public static final String SERVER = "krykpcp.desy.de";
//	public static final String USER = "kryoArchiver";
//	public static final String PASSWORD = "archive";

//krynfsb settings
	public static final String DATABASE = "archive";
	public static final String SERVER = "krynfsb.desy.de";
	public static final String USER = "cssUser";
	public static final String PASSWORD = "cssUser";
	public static final double RANGE_IN_NANO = 60*60*1e9;
//	public static final String GROUP_ID = "12";
	public static final String GROUP_ID = "5";
//	public static final int RANGE_START_IN_SECONDS = 1327937579;
//	public static final int RANGE_END_IN_SECONDS = RANGE_START_IN_SECONDS + 60*60*24;
	
//Zeitpintervall fuer Ausfaelle am 2011-11-23 zwischen 18:00 und 20:00 Kanal 12PI102_ai.VAL (group 7)
//	public static final int RANGE_START_IN_SECONDS = 1322042400;
//Zeitpintervall 2012-02-11 12:00
	public static final int RANGE_START_IN_SECONDS = 1328958000;
	public static final int RANGE_END_IN_SECONDS = RANGE_START_IN_SECONDS + 60*60*24*3;
}
