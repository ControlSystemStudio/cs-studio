/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jms.beast;

import java.text.ParseException;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VString;
import org.epics.vtype.VTypeToString;

/**
 * 
 * @author carcassi
 */
class MapMessageToVString extends AlarmTimeDisplayExtractor implements VString {

	protected final String value;

	/**
	 * @param pvField
	 * @param disconnected
	 */
	public MapMessageToVString(MapMessage message, boolean disconnected)
			throws JMSException, ParseException {
		super(message, disconnected);

		if (alarmSeverity.equals(AlarmSeverity.NONE) || message.getString(JMSAlarmMessage.VALUE)==null) {
			value = "";
		} else {
			value = message.getString(JMSAlarmMessage.VALUE);
		}
	}

	@Override
	public String getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PVFieldToVString [value=" + value + ", alarmSeverity="
				+ alarmSeverity + ", alarmStatus=" + alarmStatus
				+ ", timeStamp=" + timeStamp + ", timeUserTag=" + timeUserTag
				+ ", isTimeValid=" + isTimeValid + ", lowerDisplayLimit="
				+ lowerDisplayLimit + ", lowerCtrlLimit=" + lowerCtrlLimit
				+ ", lowerAlarmLimit=" + lowerAlarmLimit
				+ ", lowerWarningLimit=" + lowerWarningLimit + ", units="
				+ units + ", format=" + format + ", upperWarningLimit="
				+ upperWarningLimit + ", upperAlarmLimit=" + upperAlarmLimit
				+ ", upperCtrlLimit=" + upperCtrlLimit + ", upperDisplayLimit="
				+ upperDisplayLimit + "]";
	}

}
