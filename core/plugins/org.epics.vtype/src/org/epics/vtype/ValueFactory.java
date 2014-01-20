/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import org.epics.util.text.NumberFormats;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListNumber;
import org.epics.util.array.ListNumbers;
import org.epics.util.time.Timestamp;

/**
 * Factory class for all concrete implementation of the types.
 * <p>
 * The factory methods do not do anything in terms of defensive copy and
 * immutability to the objects, which they are passed as they are. It's the
 * client responsibility to prepare them appropriately, which is automatically
 * done anyway for all objects except collections.
 *
 * @author carcassi
 */
public class ValueFactory {
    
    /**
     * Creates a new VString.
     * 
     * @param value the string value
     * @param alarm the alarm
     * @param time the time
     * @return the new value
     */
    public static VString newVString(final String value, final Alarm alarm, final Time time) {
        return new IVString(value, alarm, time);
    }
    
    /**
     * Creates a new VBoolean.
     * 
     * @param value the boolean value
     * @param alarm the alarm
     * @param time the time
     * @return the new value
     */
    public static VBoolean newVBoolean(final boolean value, final Alarm alarm, final Time time) {
        return new IVBoolean(value, alarm, time);
    }

    
    /**
     * Creates a new VMultiDouble.
     * 
     * @param values the values
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    public static VMultiDouble newVMultiDouble(List<VDouble> values, final Alarm alarm, final Time time, final Display display) {
        return new IVMultiDouble(values, alarm, time, display);
    }


    /**
     * Creates a new VInt.
     * 
     * @param value the value
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    public static VInt newVInt(final Integer value, final Alarm alarm, final Time time, final Display display) {
        return new IVInt(value, alarm, time, display);
    }
    
    /**
     * New alarm with the given severity and status.
     * 
     * @param alarmSeverity the alarm severity
     * @param alarmName the alarm name
     * @return the new alarm
     */
    public static Alarm newAlarm(final AlarmSeverity alarmSeverity, final String alarmName) {
        return new Alarm() {

            @Override
            public AlarmSeverity getAlarmSeverity() {
                return alarmSeverity;
            }

            @Override
            public String getAlarmName() {
                return alarmName;
            }

            @Override
            public String toString() {
                return VTypeToString.alarmToString(this);
            }
            
        };
    }
    
    private static final Alarm alarmNone = newAlarm(AlarmSeverity.NONE, "NONE");
    private static final Display displayBoolean = newDisplay(0.0, 0.0, 0.0, "", NumberFormats.toStringFormat(),
            1.0, 1.0, 1.0, 0.0, 1.0);
    
    /**
     * No alarm.
     * 
     * @return severity and status NONE
     */
    public static Alarm alarmNone() {
        return alarmNone;
    }
    
    /**
     * Alarm based on the value and the display ranges.
     * 
     * @param value the value
     * @param display the display information
     * @return the new alarm
     */
    public static Alarm newAlarm(Number value, Display display) {
        // Calculate new AlarmSeverity, using display ranges
        AlarmSeverity severity = AlarmSeverity.NONE;
        String status = "NONE";
        if (value.doubleValue() <= display.getLowerAlarmLimit()) {
            status = "LOLO";
            severity = AlarmSeverity.MAJOR;
        } else if (value.doubleValue() >= display.getUpperAlarmLimit()) {
            status = "HIHI";
            severity = AlarmSeverity.MAJOR;
        } else if (value.doubleValue() <= display.getLowerWarningLimit()) {
            status = "LOW";
            severity = AlarmSeverity.MINOR;
        } else if (value.doubleValue() >= display.getUpperWarningLimit()) {
            status = "HIGH";
            severity = AlarmSeverity.MINOR;
        }
        
        return newAlarm(severity, status);
    }
    
    /**
     * Creates a new time.
     * 
     * @param timestamp the timestamp
     * @param timeUserTag the user tag
     * @param timeValid whether the time is valid
     * @return the new time
     */
    public static Time newTime(final Timestamp timestamp, final Integer timeUserTag, final boolean timeValid) {
        return new Time() {

            @Override
            public Timestamp getTimestamp() {
                return timestamp;
            }

            @Override
            public Integer getTimeUserTag() {
                return timeUserTag;
            }

            @Override
            public boolean isTimeValid() {
                return timeValid;
            }
        };
    }
    
    /**
     * New time, with no user tag and valid data.
     * 
     * @param timestamp the timestamp
     * @return the new time
     */
    public static Time newTime(final Timestamp timestamp) {
        return newTime(timestamp, null, true);
    }
    
    /**
     * New time with the current timestamp, no user tag and valid data.
     * 
     * @return the new time
     */
    public static Time timeNow() {
        return newTime(Timestamp.now(), null, true);
    }
    
    /**
     * Creates a new display
     * 
     * @param lowerDisplayLimit lower display limit
     * @param lowerAlarmLimit lower alarm limit
     * @param lowerWarningLimit lower warning limit
     * @param units the units
     * @param numberFormat the formatter
     * @param upperWarningLimit the upper warning limit
     * @param upperAlarmLimit the upper alarm limit
     * @param upperDisplayLimit the upper display limit
     * @param lowerCtrlLimit the lower control limit
     * @param upperCtrlLimit the upper control limit
     * @return the new display
     */
    public static Display newDisplay(final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new Display() {
            @Override
            public Double getLowerCtrlLimit() {
                return lowerCtrlLimit;
            }

            @Override
            public Double getUpperCtrlLimit() {
                return upperCtrlLimit;
            }

            @Override
            public Double getLowerDisplayLimit() {
                return lowerDisplayLimit;
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
                return numberFormat;
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
            public Double getUpperDisplayLimit() {
                return upperDisplayLimit;
            }

        };
    }
    
    public static ArrayDimensionDisplay newDisplay(final ListNumber boundaries, final String unit) {
        return new ArrayDimensionDisplay() {

            @Override
            public ListNumber getCellBoundaries() {
                return boundaries;
            }

            @Override
            public String getUnits() {
                return unit;
            }
        };
    }
    
    /**
     * Returns an array display where the index is used to calculate the
     * cell boundaries.
     * 
     * @param nCells the number of cells along the direction
     * @return a new array display
     */
    public static ArrayDimensionDisplay newDisplay(int nCells) {
        final ListNumber boundaries = ListNumbers.linearList(0, 1, nCells + 1);
        return newDisplay(boundaries, "");
    }
    
    private static final Display displayNone = newDisplay(Double.NaN, Double.NaN, 
            Double.NaN, "", NumberFormats.toStringFormat(), Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN);
    
    /**
     * Empty display information.
     * 
     * @return no display
     */
    public static Display displayNone() {
        return displayNone;
    }
    
    /**
     * Returns a display from 0 to 1, suitable for booleans.
     * 
     * @return a display for boolean
     */
    public static Display displayBoolean() {
        return displayBoolean;
    }
    
    /**
     * Creates a new VNumber based on the type of the data
     * 
     * @param value
     * @param alarm
     * @param time
     * @param display
     * @return
     */
    public static VNumber newVNumber(Number value, Alarm alarm, Time time, Display display){
	if(value instanceof Double){
	    return newVDouble((Double) value, alarm, time, display);
	}else if(value instanceof Integer){
	    return newVInt((Integer)value, alarm, time, display);
	}	
	throw new UnsupportedOperationException();
    }
    
    /**
     * Creates a new VDouble.
     * 
     * @param value the value
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    public static VDouble newVDouble(final Double value, final Alarm alarm, final Time time, final Display display) {
        return new IVDouble(value, alarm, time, display);
    }

    /**
     * Creates a new VDouble using the given value, time, display and
     * generating the alarm from the value and display information.
     * 
     * @param value the new value
     * @param time the time
     * @param display the display information
     * @return the new value
     */
    public static VDouble newVDouble(Double value, Time time, Display display) {
        return newVDouble(value, newAlarm(value, display), time, display);
    }
    
    /**
     * Creates new immutable VDouble by using metadata from the old value,
     * now as timestamp and computing alarm from the metadata range.
     * 
     * @param value new numeric value
     * @param display metadata
     * @return new value
     */
    public static VDouble newVDouble(Double value, Display display) {
        return newVDouble(value, timeNow(), display);
    }
    
    /**
     * Creates a new VDouble, no alarm, time now, no display.
     * 
     * @param value the value
     * @return the new value
     */
    public static VDouble newVDouble(Double value) {
        return newVDouble(value, alarmNone(), timeNow(), displayNone());
    }
    
    /**
     * Creates a new VDouble, no alarm, no display.
     * 
     * @param value the value
     * @param time the time
     * @return the new value
     */
    public static VDouble newVDouble(Double value, Time time) {
        return newVDouble(value, alarmNone(), time, displayNone());
    }

    /**
     * Create a new VEnum.
     * 
     * @param index the index in the label array
     * @param labels the labels
     * @param alarm the alarm
     * @param time the time
     * @return the new value
     */
    public static VEnum newVEnum(int index, List<String> labels, Alarm alarm, Time time) {
        return new IVEnum(index, labels, alarm, time);
    }

    /**
     * Creates a new VStatistics.
     * 
     * @param average average
     * @param stdDev standard deviation
     * @param min minimum
     * @param max maximum
     * @param nSamples number of samples
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value 
     */
    public static VStatistics newVStatistics(final double average, final double stdDev,
            final double min, final double max, final int nSamples, final Alarm alarm,
            final Time time, final Display display) {
        return new IVStatistics(average, stdDev, min, max, nSamples,
                alarm, time, display);
    }
    
    /**
     * Creates a new VDoubleArray.
     * 
     * @param values array values
     * @param sizes sizes
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    @Deprecated
    public static VDoubleArray newVDoubleArray(final double[] values, final ListInt sizes, Alarm alarm, Time time, Display display) {
        return new IVDoubleArray(new ArrayDouble(values), sizes, alarm, time, display);
    }
    
    /**
     * Creates a new VNumberArray based on the type of the data.
     * 
     * @param data
     * @param alarm
     * @param time
     * @param display
     * @return
     */
    public static VNumberArray newVNumberArray(final ListNumber data, final Alarm alarm, final Time time, final Display display){
	if(data instanceof ListDouble){
	    return newVDoubleArray((ListDouble)data, alarm, time, display);
	}else if(data instanceof ListInt){
	    return newVIntArray((ListInt)data, alarm, time, display);
	}	
	throw new UnsupportedOperationException("TODO: support types other than double and int");
    }
    
    /**
     * Creates a new VNumberArray based on the type of the data.
     * 
     * @param data
     * @param alarm
     * @param time
     * @param display
     * @return
     */
    public static VNumberArray newVNumberArray(final ListNumber data, final ListInt sizes, final List<ArrayDimensionDisplay> dimensionDisplay,
            final Alarm alarm, final Time time, final Display display){
	if (data instanceof ListDouble){
	    return new IVDoubleArray((ListDouble) data, sizes, dimensionDisplay, alarm, time, display);
	} else if(data instanceof ListInt){
	    return new IVIntArray((ListInt)data, sizes, dimensionDisplay, alarm, time, display);
	}	
	throw new UnsupportedOperationException("TODO: support types other than double and int");
    }
    
    /**
     * Creates a new VDoubleArray.
     * 
     * @param values array values
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    @Deprecated
    public static VDoubleArray newVDoubleArray(final double[] values, Alarm alarm, Time time, Display display) {
        return newVDoubleArray(values, new ArrayInt(values.length), alarm, time, display);
    }
    
    /**
     * Creates a new VDoubleArray.
     * 
     * @param data array data
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    public static VDoubleArray newVDoubleArray(ListDouble data, Alarm alarm, Time time, Display display) {
        return new IVDoubleArray(data, new ArrayInt(data.size()), alarm,
                time, display);
    }
    
    /**
     * Creates a new VFloatArray.
     * 
     * @param data array data
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    public static VFloatArray newVFloatArray(ListFloat data, Alarm alarm, Time time, Display display) {
        return new IVFloatArray(data, new ArrayInt(data.size()), alarm,
                time, display);
    }
    
    /**
     * Creates a new VDoubleArray.
     * 
     * @param values array values
     * @param display the display
     * @return the new value
     */
    @Deprecated
    public static VDoubleArray newVDoubleArray(final double[] values, Display display) {
        return newVDoubleArray(values, new ArrayInt(values.length), alarmNone(), timeNow(), display);
    }

    /**
     * Creates a new VImage given the data and the size.
     * 
     * @param height the height
     * @param width the width
     * @param data the data
     * @return a new object
     */
    public static VImage newVImage(int height, int width, byte[] data) {
        return new IVImage(height, width, data);
    }
    
    /**
     * Creates a new VIntArray.
     * 
     * @param values array values
     * @param alarm the alarm
     * @param time the time
     * @param display the display
     * @return the new value
     */
    public static VIntArray newVIntArray(final ListInt values, Alarm alarm, Time time, Display display) {
        return new IVIntArray(values, new ArrayInt(values.size()), alarm, time, display);
    }

    /**
     * Create a new VEnumArray.
     * 
     * @param indexes the indexes in the label array
     * @param labels the labels
     * @param alarm the alarm
     * @param time the time
     * @return the new value
     */
    public static VEnumArray newVEnumArray(ListInt indexes, List<String> labels, Alarm alarm, Time time) {
        return new IVEnumArray(indexes, labels, new ArrayInt(indexes.size()), alarm, time);
    }

    /**
     * Creates a new VStringArray.
     * 
     * @param data the strings
     * @param alarm the alarm
     * @param time the time
     * @return the new value
     */
    public static VStringArray newVStringArray(List<String> data, Alarm alarm, Time time) {
        return new IVStringArray(data, new ArrayInt(data.size()), alarm, time);
    }
    
    /**
     * Creates a new VTable - this method is provisional and will change in the future.
     * 
     * @param types the types for each column
     * @param names the names for each column
     * @param values the values for each column
     * @return the new value
     */
    public static VTable newVTable(List<Class<?>> types, List<String> names, List<Object> values) {
        return new IVTable(types, names, values);
    }
    
    /**
     * Takes a java objects and wraps it into a vType. All numbers are wrapped
     * as VDouble. String is wrapped as VString. double[] and ListDouble are wrapped as
     * VDoubleArray. A List of String is wrapped to a VStringArray. Alarms
     * are alarmNone(), time are timeNow() and display are displayNone();
     * 
     * @param value the value to wrap
     * @return the wrapped value
     */
    public static VType wrapValue(Object value) {
        return wrapValue(value, alarmNone());
    }
    
    /**
     * Takes a java objects and wraps it into a vType. All numbers are wrapped
     * as VDouble. String is wrapped as VString. double[] and ListDouble are wrapped as
     * VDoubleArray. A List of String is wrapped to a VStringArray. Alarms
     * are alarm, time are timeNow() and display are displayNone();
     * 
     * @param value the value to wrap
     * @param alarm the alarm for the value
     * @return the wrapped value
     */
    public static VType wrapValue(Object value, Alarm alarm) {
        if (value instanceof Number) {
            // Special support for numbers
            return newVDouble(((Number) value).doubleValue(), alarm, timeNow(),
                    displayNone());
        } else if (value instanceof String) {
            // Special support for strings
            return newVString(((String) value),
                    alarm, timeNow());
        } else if (value instanceof double[]) {
            return newVDoubleArray(new ArrayDouble((double[]) value), alarm, timeNow(), displayNone());
        } else if (value instanceof ListDouble) {
            return newVDoubleArray((ListDouble) value, alarm, timeNow(), displayNone());
        } else if (value instanceof List) {
            boolean matches = true;
            List list = (List) value;
            for (Object object : list) {
                if (!(object instanceof String)) {
                    matches = false;
                }
            }
            if (matches) {
                @SuppressWarnings("unchecked")
                List<String> newList = (List<String>) list;
                return newVStringArray(Collections.unmodifiableList(newList), alarm, timeNow());
            } else {
                throw new UnsupportedOperationException("Type " + value.getClass().getName() + " contains non Strings");
            }
        } else {
            // TODO: need to implement all the other arrays
            throw new UnsupportedOperationException("Type " + value.getClass().getName() + "  is not yet supported");
        }
    }
}
