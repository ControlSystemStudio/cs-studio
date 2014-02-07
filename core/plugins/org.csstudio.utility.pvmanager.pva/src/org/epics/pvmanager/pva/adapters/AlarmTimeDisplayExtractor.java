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
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.util.text.NumberFormats;
import org.epics.vtype.Display;

public class AlarmTimeDisplayExtractor extends AlarmTimeExtractor implements Display {

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
	
	private static final String noUnits = "";
	private static final Double noLimit = 0.0;
	
	public AlarmTimeDisplayExtractor(PVStructure pvField, boolean disconnected)
	{
		super(pvField, disconnected);
		
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
