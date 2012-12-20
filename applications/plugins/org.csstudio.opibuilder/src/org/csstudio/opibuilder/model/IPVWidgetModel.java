package org.csstudio.opibuilder.model;

public interface IPVWidgetModel {
	/**
	 * If this is true, an corresponding alarm border will show up when the alarm status
	 * of input PV is not normal. 
	 * The default Major alarm border is red bold line border. Minor alarm border
	 * is orange bold line border. Invalid alarm border is pink bold line border.
	 * The alarm colors can be redefined in color macro file.  
	 */
	public static final String PROP_BORDER_ALARMSENSITIVE= "border_alarm_sensitive"; //$NON-NLS-1$
	
	/**
	 * If this is true, the foreground color will change depends on the alarm status of input PV.
	 * The default major color is red, minor color is orange and invalid color is pink.
	 * The alarm colors can be redefined in color macro file.   
	 */
	public static final String PROP_FORECOLOR_ALARMSENSITIVE= "forecolor_alarm_sensitive"; //$NON-NLS-1$

	/**
	 * If this is true, the background color will change depends on the alarm status of input PV.
	 * The default major color is red, minor color is orange and invalid color is pink.
	 * The alarm colors can be redefined in color macro file.   
	 */
	public static final String PROP_BACKCOLOR_ALARMSENSITIVE= "backcolor_alarm_sensitive"; //$NON-NLS-1$
	/**
	 * The property which hold the value of input PV.
	 */
	public static final String PROP_PVVALUE= "pv_value"; //$NON-NLS-1$
	
	/**
	 * The name of the input PV.
	 */
	public static final String PROP_PVNAME= "pv_name"; //$NON-NLS-1$
	
	public boolean isBorderAlarmSensitve();
	
	public boolean isForeColorAlarmSensitve();
	
	public boolean isBackColorAlarmSensitve();
	
	public String getPVName();
	
}
