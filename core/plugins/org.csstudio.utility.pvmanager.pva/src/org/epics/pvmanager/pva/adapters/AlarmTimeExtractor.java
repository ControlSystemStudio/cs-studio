/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVLong;
import org.epics.pvdata.pv.PVStructure;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;

public class AlarmTimeExtractor implements Alarm, Time {

	protected final AlarmSeverity alarmSeverity;
	protected final String alarmStatus;
	protected final Timestamp timeStamp;
	protected final Integer timeUserTag;
	protected final boolean isTimeValid;
	
	private static final Timestamp noTimeStamp = org.epics.util.time.Timestamp.of(0,0);
	private static final Integer noTimeUserTag = null;
	
	public AlarmTimeExtractor(PVStructure pvField, boolean disconnected)
	{
		// alarm_t
		if (disconnected)
		{
			alarmSeverity = AlarmSeverity.UNDEFINED;
			alarmStatus = "CLIENT";
		}
		else
		{
			PVStructure alarmStructure = pvField.getStructureField("alarm");
			if (alarmStructure != null)
			{
				PVInt severityField = alarmStructure.getIntField("severity");
				if (severityField == null)
					alarmSeverity = AlarmSeverity.UNDEFINED;
				else
					alarmSeverity = alarmSeverityMapLUT[severityField.get()];
				// no explicit out-of-bounds check
				
				
				PVInt statusField = alarmStructure.getIntField("status");
				if (statusField == null)
					alarmStatus = "UNDEFINED";
				else
					alarmStatus = alarmStatusMapLUT[statusField.get()];
				// no explicit out-of-bounds check
				
			}
			else
			{
				alarmSeverity = AlarmSeverity.UNDEFINED;
				alarmStatus = "UNDEFINED";
			}
		}
		
		// timeStamp_t
		PVStructure timeStampStructure = pvField.getStructureField("timeStamp");
		if (timeStampStructure != null)
		{
			PVLong secsField = timeStampStructure.getLongField("secondsPastEpoch");
			PVInt nanosField = timeStampStructure.getIntField("nanoSeconds");
			
			if (secsField == null || nanosField == null)
				timeStamp = noTimeStamp;
			else
				timeStamp = org.epics.util.time.Timestamp.of(secsField.get(), nanosField.get());
			
			PVInt userTagField = timeStampStructure.getIntField("userTag");
			if (userTagField == null)
				timeUserTag = noTimeUserTag;
			else
				timeUserTag = userTagField.get();
			
			isTimeValid = (timeStamp != null);
		}
		else
		{
			timeStamp = noTimeStamp;
			timeUserTag = null;
			isTimeValid = false;
		}
		
	}
	
	// org.epics.pvdata.property.AlarmSeverity to pvmanager.AlarmSeverity
	protected static final AlarmSeverity alarmSeverityMapLUT[] =
	{
		AlarmSeverity.NONE,
		AlarmSeverity.MINOR,
		AlarmSeverity.MAJOR,
		AlarmSeverity.INVALID,
		AlarmSeverity.UNDEFINED
	};
	
	// org.epics.pvdata.property.AlarmStatus to pvmanager.AlarmStatus
	protected static final String alarmStatusMapLUT[] =
	{
		"NONE",
		"DEVICE",
		"DRIVER",
		"RECORD",
		"DB",
		"CONF",
		"UNDEFINED",
		"CLIENT"
	};
 
	@Override
	public AlarmSeverity getAlarmSeverity() {
		return alarmSeverity;
	}

    @Override
    public String getAlarmName() {
        return alarmStatus.toString();
    }
        
        

	@Override
	public Timestamp getTimestamp() {
		return timeStamp;
	}

	@Override
	public Integer getTimeUserTag() {
		return timeUserTag;
	}

	@Override
	public boolean isTimeValid() {
		return isTimeValid;
	}

}
