/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.pvmanager.util.TimeStamp;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
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
    
    public static VString newVString(String value, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag) {
        return new IVString(value, alarmSeverity, alarmStatus, TimeStamp.asTimestamp(timeStamp), timeUserTag, true);
    }

    public static VMultiDouble newVMultiDouble(List<VDouble> values, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        return new IVMultiDouble(values, alarmSeverity, alarmStatus,
                TimeStamp.asTimestamp(timeStamp), timeUserTag, true, lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit,
                units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }
    
    public static VMultiDouble newVMultiDouble(List<VDouble> values, final Alarm alarm, final Time time, final Display display) {
        return new IVMultiDouble(values, alarm.getAlarmSeverity(), alarm.getAlarmStatus(),
                time.getTimestamp(), time.getTimeUserTag(), time.isTimeValid(),
                display.getLowerDisplayLimit(), display.getLowerCtrlLimit(), display.getLowerAlarmLimit(), display.getLowerWarningLimit(),
                display.getUnits(), display.getFormat(),
                display.getUpperWarningLimit(), display.getUpperAlarmLimit(), display.getUpperCtrlLimit(), display.getUpperDisplayLimit());
    }

    /**
     * Creates new immutable VInt.
     */
    @Deprecated
    public static VInt newVInt(final Integer value, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVInt(value, alarmSeverity, alarmStatus, TimeStamp.asTimestamp(timeStamp), timeUserTag, true, lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }
    
    /**
     * Creates new immutable VInt.
     */
    public static VInt newVInt(final Integer value, final Alarm alarm, final Time time, final Display display) {
        return new IVInt(value, alarm.getAlarmSeverity(), alarm.getAlarmStatus(),
                time.getTimestamp(), time.getTimeUserTag(), time.isTimeValid(),
                display.getLowerDisplayLimit(), display.getLowerCtrlLimit(), display.getLowerAlarmLimit(), display.getLowerWarningLimit(),
                display.getUnits(), display.getFormat(),
                display.getUpperWarningLimit(), display.getUpperAlarmLimit(), display.getUpperCtrlLimit(), display.getUpperDisplayLimit());
    }
    
    /**
     * New alarm with the given severity and status.
     * 
     * @param alarmSeverity the alarm severity
     * @param alarmStatus the alarm status
     * @return the new alarm
     */
    public static Alarm newAlarm(final AlarmSeverity alarmSeverity, final AlarmStatus alarmStatus) {
        return new Alarm() {

            @Override
            public AlarmSeverity getAlarmSeverity() {
                return alarmSeverity;
            }

            @Override
            public AlarmStatus getAlarmStatus() {
                return alarmStatus;
            }
        };
    }
    
    /**
     * No alarm.
     * 
     * @return severity and status NONE
     */
    public static Alarm alarmNone() {
        return newAlarm(AlarmSeverity.NONE, AlarmStatus.NONE);
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
        AlarmStatus status = AlarmStatus.NONE;
        if (value.doubleValue() <= display.getLowerAlarmLimit() || value.doubleValue() >= display.getUpperAlarmLimit()) {
            status = AlarmStatus.RECORD;
            severity = AlarmSeverity.MAJOR;
        } else if (value.doubleValue() <= display.getLowerWarningLimit() || value.doubleValue() >= display.getUpperWarningLimit()) {
            status = AlarmStatus.RECORD;
            severity = AlarmSeverity.MINOR;
        }
        
        return newAlarm(severity, status);
    }
    
    public static Time newTime(final Timestamp timestamp, final Integer timeUserTag, final boolean timeValid) {
        return new Time() {

            @Override
            public TimeStamp getTimeStamp() {
                return TimeStamp.timestampOf(timestamp);
            }

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
    
    public static Time newTime(final Timestamp timestamp) {
        return newTime(timestamp, null, true);
    }
    
    public static Time timeNow() {
        return newTime(Timestamp.now(), null, true);
    }
    
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
    
    public static Display displayNone() {
        return newDisplay(null, null, null, null, null, null, null, null, null, null);
    }
    
    
    /**
     * Creates new immutable VDouble.
     */
    public static VDouble newVDouble(final Double value, final Alarm alarm, final Time time, final Display display) {
        return new IVDouble(value, alarm.getAlarmSeverity(), alarm.getAlarmStatus(),
                time.getTimestamp(), time.getTimeUserTag(), time.isTimeValid(),
                display.getLowerDisplayLimit(), display.getLowerCtrlLimit(), display.getLowerAlarmLimit(), display.getLowerWarningLimit(),
                display.getUnits(), display.getFormat(),
                display.getUpperWarningLimit(), display.getUpperAlarmLimit(), display.getUpperCtrlLimit(), display.getUpperDisplayLimit());
    }

    /**
     * Creates new immutable VDouble.
     */
    @Deprecated
    public static VDouble newVDouble(final Double value, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new VDouble() {

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

            @Override
            public Integer getTimeUserTag() {
                return timeUserTag;
            }

            @Override
            public TimeStamp getTimeStamp() {
                return timeStamp;
            }

            @Override
            public Timestamp getTimestamp() {
                return TimeStamp.asTimestamp(timeStamp);
            }

            @Override
            public AlarmSeverity getAlarmSeverity() {
                return alarmSeverity;
            }

            @Override
            public AlarmStatus getAlarmStatus() {
                return alarmStatus;
            }

            @Override
            public Double getValue() {
                return value;
            }

            @Override
            public boolean isTimeValid() {
                return true;
            }
        };
    }

    /**
     * Creates new immutable new VDouble by using the metadata from the old value.
     */
    @Deprecated
    public static VDouble newVDouble(final Double value, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final Integer timeUserTag, final TimeStamp timeStamp,
            Display display) {
        return newVDouble(value, alarmSeverity, alarmStatus,
                timeStamp,
                timeUserTag,
                display.getLowerDisplayLimit(), display.getLowerAlarmLimit(),
                display.getLowerWarningLimit(), display.getUnits(),
                display.getFormat(), display.getUpperWarningLimit(),
                display.getUpperAlarmLimit(), display.getUpperDisplayLimit(),
                display.getLowerCtrlLimit(), display.getUpperCtrlLimit());
    }
    
    /**
     * Creates new immutable VDouble by using the metadata from the old value,
     * and computing the alarm from the metadata range.
     * 
     * @param value new numeric value
     * @param timeStamp time stamp
     * @param display metadata
     * @return new value
     */
    @Deprecated
    public static VDouble newVDouble(double value, TimeStamp timeStamp, Display display) {
        // Calculate new AlarmSeverity, using oldValue ranges
        AlarmSeverity severity = AlarmSeverity.NONE;
        AlarmStatus status = AlarmStatus.NONE;
        if (value <= display.getLowerAlarmLimit() || value >= display.getUpperAlarmLimit()) {
            status = AlarmStatus.RECORD;
            severity = AlarmSeverity.MAJOR;
        } else if (value <= display.getLowerWarningLimit() || value >= display.getUpperWarningLimit()) {
            status = AlarmStatus.RECORD;
            severity = AlarmSeverity.MINOR;
        }

        return ValueFactory.newVDouble(value, severity, status,
                null, timeStamp, display);
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
    @Deprecated
    public static VDouble newVDouble(double value, Display display) {
        return newVDouble(value, newTime(Timestamp.now()), display);
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
        return newVDouble(value, newTime(Timestamp.now()), display);
    }
    
    public static VDouble newVDouble(Double value) {
        return newVDouble(value, alarmNone(), newTime(Timestamp.now()), displayNone());
    }
    
    public static VDouble newVDouble(Double value, Time time) {
        return newVDouble(value, alarmNone(), time, displayNone());
    }

    /**
     * Creates a new immutable VStatistics.
     */
    public static VStatistics newVStatistics(final double average, final double stdDev,
            final double min, final double max, final int nSamples,
            final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVStatistics(average, stdDev, min, max, nSamples, alarmSeverity,
                alarmStatus, TimeStamp.asTimestamp(timeStamp), timeUserTag, true, lowerDisplayLimit, lowerCtrlLimit,
                lowerAlarmLimit, lowerWarningLimit, units, numberFormat, upperWarningLimit,
                upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    /**
     * Creates a new VStatistics by taking the metadata from a VDouble.
     */
    public static VStatistics newVStatistics(final double average, final double stdDev,
            final double min, final double max, final int nSamples, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final Integer timeUserTag, final TimeStamp timeStamp,
            VDouble aValue) {
        return newVStatistics(average, stdDev, min, max, nSamples,
                alarmSeverity, alarmStatus,
                timeStamp,
                timeUserTag,
                aValue.getLowerDisplayLimit(), aValue.getLowerAlarmLimit(),
                aValue.getLowerWarningLimit(), aValue.getUnits(),
                aValue.getFormat(), aValue.getUpperWarningLimit(),
                aValue.getUpperAlarmLimit(), aValue.getUpperDisplayLimit(),
                aValue.getLowerCtrlLimit(), aValue.getUpperCtrlLimit());
    }

    /**
     * Creates new immutable VInt.
     */
    public static VInt newEInt(final Integer value, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVInt(value, alarmSeverity, alarmStatus, TimeStamp.asTimestamp(timeStamp), timeUserTag, true, lowerDisplayLimit,
                lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat, upperWarningLimit,
                upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    /**
     * Creates new immutable newDbrCtrlInt by using the metadata from the old value.
     */
    public static VInt newEInt(final Integer value, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final Integer timeUserTag, final TimeStamp timeStamp,
            VInt oldValue) {
        return newEInt(value, alarmSeverity, alarmStatus,
                timeStamp,
                timeUserTag,
                oldValue.getLowerDisplayLimit(), oldValue.getLowerAlarmLimit(),
                oldValue.getLowerWarningLimit(), oldValue.getUnits(),
                oldValue.getFormat(), oldValue.getUpperWarningLimit(),
                oldValue.getUpperAlarmLimit(), oldValue.getUpperDisplayLimit(),
                oldValue.getLowerCtrlLimit(), oldValue.getUpperCtrlLimit());
    }
    
    public static VDoubleArray newVDoubleArray(final double[] values, final List<Integer> sizes, Alarm alarm, Time time, Display display) {
        return new IVDoubleArray(values, sizes, alarm.getAlarmSeverity(), alarm.getAlarmStatus(),
                time.getTimestamp(), time.getTimeUserTag(), time.isTimeValid(),
                display.getLowerDisplayLimit(), display.getLowerCtrlLimit(), display.getLowerAlarmLimit(), display.getLowerWarningLimit(),
                display.getUnits(), display.getFormat(),
                display.getUpperWarningLimit(), display.getUpperAlarmLimit(), display.getUpperCtrlLimit(), display.getUpperDisplayLimit());
    }
    
    public static VDoubleArray newVDoubleArray(final double[] values, Alarm alarm, Time time, Display display) {
        return newVDoubleArray(values, Collections.singletonList(values.length), alarm, time, display);
    }
    
    public static VDoubleArray newVDoubleArray(ListDouble data, Alarm alarm, Time time, Display display) {
        return new IVDoubleArray(data, Collections.singletonList(data.size()), alarm,
                time, display);
    }
    
    public static VDoubleArray newVDoubleArray(final double[] values, Display display) {
        return newVDoubleArray(values, Collections.singletonList(values.length), alarmNone(), newTime(Timestamp.now()), display);
    }

    @Deprecated
    public static VDoubleArray newVDoubleArray(final double[] values, final List<Integer> sizes, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVDoubleArray(values, sizes, alarmSeverity, alarmStatus, TimeStamp.asTimestamp(timeStamp), timeUserTag, true,
                lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat,
                upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    public static VImage newVImage(int height, int width, byte[] data) {
        return new IVImage(height, width, data);
    }
    
    public static VIntArray newVIntArray(final int[] values, final List<Integer> sizes, Alarm alarm, Time time, Display display) {
        return new IVIntArray(values, sizes, alarm.getAlarmSeverity(), alarm.getAlarmStatus(),
                time.getTimestamp(), time.getTimeUserTag(), time.isTimeValid(),
                display.getLowerDisplayLimit(), display.getLowerCtrlLimit(), display.getLowerAlarmLimit(), display.getLowerWarningLimit(),
                display.getUnits(), display.getFormat(),
                display.getUpperWarningLimit(), display.getUpperAlarmLimit(), display.getUpperCtrlLimit(), display.getUpperDisplayLimit());
    }
    
    public static VIntArray newVIntArray(final int[] values, Alarm alarm, Time time, Display display) {
        return newVIntArray(values, Collections.singletonList(values.length), alarm, time, display);
    }
    
    public static VIntArray newVIntArray(final ListInt values, Alarm alarm, Time time, Display display) {
        return new IVIntArray(values, Collections.singletonList(values.size()), alarm,
                time, display);
    }
    
    public static VIntArray newVIntArray(final int[] values, Display display) {
        return newVIntArray(values, Collections.singletonList(values.length), alarmNone(), newTime(Timestamp.now()), display);
    }

    static VIntArray newVIntArray(final int[] values, final List<Integer> sizes, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVIntArray(values, sizes, alarmSeverity, alarmStatus, TimeStamp.asTimestamp(timeStamp), timeUserTag, true,
                lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat,
                upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

}
