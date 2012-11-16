package org.csstudio.archive.common.guard;


public class Settings {
	
//krykpcp settings
//	public static final String DATABASE = "archiveengine_test";
//	public static final String SERVER = "krykpcp.desy.de";
//	public static final String USER = "kryoArchiver";
//	public static final String PASSWORD = "archive";

//krynfsb settings
	public static final String DATABASE = "archive";
	public static final String SERVER = "krynfsa.desy.de";
	public static final String USER = "cssUser";
	public static final String PASSWORD = "cssUser";
	public static final double RANGE_IN_NANO = 60*30*1e9;
//	public static final String GROUP_ID = "12"; //Wetter
//	public static final String GROUP_ID = "5";
//	public static final String GROUP_ID = "3"; //Kryo
//	public static final String GROUP_ID = "7"; //Kryo2
	public static final String GROUP_ID = "4"; //MKK
//	public static final int RANGE_START_IN_SECONDS = 1327937579;
//	public static final int RANGE_END_IN_SECONDS = RANGE_START_IN_SECONDS + 60*60*24;
	
	//Zeitpintervall fuer Ausfaelle am 2011-11-23 zwischen 18:00 und 20:00 Kanal 12PI102_ai (group 7)
//	public static final int RANGE_START_IN_SECONDS = 1322042400;
	//Zeitpintervall fuer Ausfaelle am 2012-02-21 zwischen 06:00 und 12:00 Kanal 20MFG_U23_li (group 7)
//	public static final int RANGE_START_IN_SECONDS = ?;
//Zeitpintervall 2012-02-11 12:00
//	2012-09-11 8:00  Beginn Wartungstag mit IOC Boots: 1347343200 
//	public static final int RANGE_START_IN_SECONDS = 1322042400;
//	public static final int RANGE_END_IN_SECONDS = RANGE_START_IN_SECONDS + 60*60*12*1;
	public static final int RANGE_START_IN_SECONDS = 1352869200;
	public static final int RANGE_END_IN_SECONDS = RANGE_START_IN_SECONDS + 60*60*6*1;
}
