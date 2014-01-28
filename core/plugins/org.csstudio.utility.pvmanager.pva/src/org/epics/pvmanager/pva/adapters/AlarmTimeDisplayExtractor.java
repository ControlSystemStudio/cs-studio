/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<String, NumberFormat> formatterCache =
            new ConcurrentHashMap<String, NumberFormat>();

    private static NumberFormat createNumberFormat(String printfFormat)
	{
		if (printfFormat == null ||
			printfFormat.isEmpty() ||
			printfFormat.trim().isEmpty() ||
			printfFormat.equals("%s"))
			return NumberFormats.toStringFormat();
		else
		{
			NumberFormat formatter = formatterCache.get(printfFormat);
			if (formatter != null)
				return formatter;
			else
			{
				formatter = new PrintfFormat(printfFormat);
				formatterCache.put(printfFormat, formatter);
				return formatter;
			}
		}
	}

	@SuppressWarnings("serial")
	static class PrintfFormat extends java.text.NumberFormat
    {
    	private final String format;
    	public PrintfFormat(String printfFormat)
    	{
			// probe format
			boolean allOK = true;
			try {
				String.format(printfFormat, 0.0);
			} catch (Throwable th) {
				allOK = false;
			}
			// accept it if all is OK
			this.format = allOK ? printfFormat : null;
    	}

    	private final String internalFormat(double number)
    	{
    		if (format != null)
    			return String.format(format, number);
    		else
    			return String.valueOf(number);
    	}
    	
        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            toAppendTo.append(internalFormat(number));
            return toAppendTo;
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            toAppendTo.append(internalFormat(number));
            return toAppendTo;
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };
	
	
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
	
	private static final Timestamp noTimeStamp = org.epics.util.time.Timestamp.of(0,0);
	private static final Integer noTimeUserTag = null;
	private static final String noUnits = "";
	private static final Double noLimit = 0.0;
	
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
		
		
		// display_t
		PVStructure displayStructure = pvField.getStructureField("display");
		if (displayStructure != null)
		{
			lowerDisplayLimit = getDoubleValue(displayStructure, "limitLow", noLimit);
			upperDisplayLimit = getDoubleValue(displayStructure, "limitHigh", noLimit);
			
			PVString formatField = displayStructure.getStringField("format");
			if (formatField == null)
				format = NumberFormats.toStringFormat();
			else
				format = createNumberFormat(formatField.get());

			PVString unitsField = displayStructure.getStringField("units");
			if (unitsField == null)
				units = noUnits;
			else
				units = unitsField.get();
		}
		else
		{
			lowerDisplayLimit = null;	
			upperDisplayLimit = null;
			format = NumberFormats.toStringFormat();
			units = noUnits;
		}
	
		// control_t
		PVStructure controlStructure = pvField.getStructureField("control");
		if (controlStructure != null)
		{
			lowerCtrlLimit = getDoubleValue(controlStructure, "limitLow", noLimit);
			upperCtrlLimit = getDoubleValue(controlStructure, "limitHigh", noLimit);
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
			lowerAlarmLimit = getDoubleValue(valueAlarmStructure, "lowAlarmLimit", Double.NaN);
			lowerWarningLimit = getDoubleValue(valueAlarmStructure, "lowWarningLimit", Double.NaN);
			upperWarningLimit = getDoubleValue(valueAlarmStructure, "highWarningLimit", Double.NaN);
			upperAlarmLimit = getDoubleValue(valueAlarmStructure, "highAlarmLimit", Double.NaN);
		}
		else
		{
			lowerAlarmLimit = Double.NaN;
			lowerWarningLimit = Double.NaN;
			upperWarningLimit = Double.NaN;
			upperAlarmLimit = Double.NaN;
		}
	}
	
	protected static final Convert convert = ConvertFactory.getConvert();
	
	protected static final Double getDoubleValue(PVStructure structure, String fieldName, Double defaultValue)
	{
		PVField field = structure.getSubField(fieldName);
		if (field instanceof PVScalar)
		{
			return convert.toDouble((PVScalar)field);
		}
		else
			return defaultValue;
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
