/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;

import java.text.NumberFormat;

import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVLong;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.util.text.NumberFormats;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.util.time.Timestamp;

public class AlarmTimeDisplayExtractor implements Alarm, Time, Display {
	
	protected final AlarmSeverity alarmSeverity;
	protected final String alarmStatus;
	protected final Timestamp timeStamp;
	protected final Integer timeUserTag;
	protected final boolean isTimeValid;
	protected final Double lowerDisplayLimit;
	protected final Double lowerCtrlLimit;
	protected final Double lowerAlarmLimit;
	protected final Double lowerWarningLimit;
	protected final String units;
	protected final NumberFormat format;
	protected final Double upperWarningLimit;
	protected final Double upperAlarmLimit;
	protected final Double upperCtrlLimit;
	protected final Double upperDisplayLimit;
	
	public AlarmTimeDisplayExtractor(PVStructure pvField, boolean disconnected)
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
				timeStamp = null;
			else
				timeStamp = org.epics.util.time.Timestamp.of(secsField.get(), nanosField.get());
			
			PVInt userTagField = timeStampStructure.getIntField("userTag");
			if (userTagField == null)
				timeUserTag = null;
			else
				timeUserTag = userTagField.get();
			
			isTimeValid = (timeStamp != null);
		}
		else
		{
			timeStamp = null;
			timeUserTag = null;
			isTimeValid = false;
		}
		
		
		// display_t
		PVStructure displayStructure = pvField.getStructureField("display");
		if (displayStructure != null)
		{
			lowerDisplayLimit = getDoubleValue(displayStructure, "limitLow");
			upperDisplayLimit = getDoubleValue(displayStructure, "limitHigh");
			
			PVString formatField = displayStructure.getStringField("format");
			if (formatField == null)
				format = NumberFormats.toStringFormat();
			else
			{
				format = NumberFormats.toStringFormat();
				// TODO format = NumberFormat from formatField.get();
			}

			PVString unitsField = displayStructure.getStringField("units");
			if (unitsField == null)
				units = null;
			else
				units = unitsField.get();
		}
		else
		{
			lowerDisplayLimit = null;	
			upperDisplayLimit = null;
			format = NumberFormats.toStringFormat();
			units = null;
		}
	
		// control_t
		PVStructure controlStructure = pvField.getStructureField("control");
		if (controlStructure != null)
		{
			lowerCtrlLimit = getDoubleValue(controlStructure, "limitLow");
			upperCtrlLimit = getDoubleValue(controlStructure, "limitHigh");
		}
		else
		{
			lowerCtrlLimit = null;
			upperCtrlLimit = null;
		}
		
		
		// valueAlarm_t
		PVStructure valueAlarmStructure = pvField.getStructureField("valueAlarm");
		if (valueAlarmStructure != null)
		{
			lowerAlarmLimit = getDoubleValue(valueAlarmStructure, "lowAlarmLimit");
			lowerWarningLimit = getDoubleValue(valueAlarmStructure, "lowWarningLimit");
			upperWarningLimit = getDoubleValue(valueAlarmStructure, "highWarningLimit");
			upperAlarmLimit = getDoubleValue(valueAlarmStructure, "highAlarmLimit");
		}
		else
		{
			lowerAlarmLimit = null;
			lowerWarningLimit = null;
			upperWarningLimit = null;
			upperAlarmLimit = null;
		}
	}
	
	protected static final Convert convert = ConvertFactory.getConvert();
	
	protected static final Double getDoubleValue(PVStructure structure, String fieldName)
	{
		PVField field = structure.getSubField(fieldName);
		if (field instanceof PVScalar)
		{
			return convert.toDouble((PVScalar)field);
		}
		else
			return null;
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

	@Override
	public Double getLowerDisplayLimit() {
		return lowerDisplayLimit;
	}

	@Override
	public Double getLowerCtrlLimit() {
		return lowerCtrlLimit;
	}

	@Override
	public Double getLowerAlarmLimit() {
		return lowerAlarmLimit;
	}

	@Override
	public Double getLowerWarningLimit() {
		return lowerWarningLimit;
	}

	@Override
	public String getUnits() {
		return units;
	}

	@Override
	public NumberFormat getFormat() {
		return format;
	}

	@Override
	public Double getUpperWarningLimit() {
		return upperWarningLimit;
	}

	@Override
	public Double getUpperAlarmLimit() {
		return upperAlarmLimit;
	}

	@Override
	public Double getUpperCtrlLimit() {
		return upperCtrlLimit;
	}

	@Override
	public Double getUpperDisplayLimit() {
		return upperDisplayLimit;
	}
}
