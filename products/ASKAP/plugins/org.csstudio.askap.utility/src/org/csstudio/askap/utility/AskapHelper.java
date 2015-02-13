package org.csstudio.askap.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class AskapHelper {

	
	enum DateTimeFormat {
		LOCAL,
		UTC
	}
	private static final SimpleDateFormat DEFAULT_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
	
	/**
	 * This method returns the date time in the configured timezone (LOCAL/UTC) formated with given format 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getFormatedData(Date date, String format) {
		Date theDate = date;
		SimpleDateFormat formatter = null;
		
		if (date==null)
			theDate = new Date();
		
		if (format==null)
			formatter = DEFAULT_FORMATTER;
		else
			formatter = new SimpleDateFormat(format);
		
		if (DateTimeFormat.UTC.equals(Preferences.getDateTimeFormat())) {
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			return formatter.format(theDate);
		}
			
		return formatter.format(theDate);
	}
	
	public static long getDate(int year, int month, int day, int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		
		if (DateTimeFormat.UTC.equals(Preferences.getDateTimeFormat()))
			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		cal.set(year, month, day, hour, minute, second);
		
		return cal.getTimeInMillis();
	}
	
	public static int[] getDate(long millisec) {
		int timeField[] = new int[6];
		
		Calendar cal = Calendar.getInstance();
		
		if (DateTimeFormat.UTC.equals(Preferences.getDateTimeFormat()))
			cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		cal.setTimeInMillis(millisec);
		
		timeField[0] = cal.get(Calendar.YEAR);
		timeField[1] = cal.get(Calendar.MONTH);
		timeField[2] = cal.get(Calendar.DAY_OF_MONTH);
		timeField[3] = cal.get(Calendar.HOUR_OF_DAY);
		timeField[4] = cal.get(Calendar.MINUTE);
		timeField[5] = cal.get(Calendar.SECOND);
		
		return timeField;
	}
	
	public static Map<String, String> propertiesToParameterMap(Properties prop) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (prop != null) {
			for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				paramMap.put(key, prop.getProperty(key));
			}
		}
		
		return paramMap;
	}
}
