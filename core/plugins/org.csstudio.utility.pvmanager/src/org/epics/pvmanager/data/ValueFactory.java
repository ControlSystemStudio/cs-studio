/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import org.epics.pvmanager.util.TimeStamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

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
        return new IVString(value, alarmSeverity, alarmStatus, timeStamp, timeUserTag, true);
    }

    public static VMultiDouble newVMultiDouble(List<VDouble> values, AlarmSeverity alarmSeverity,
            AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        return new IVMultiDouble(values, alarmSeverity, alarmStatus,
                timeStamp, timeUserTag, true, lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit,
                units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    /**
     * Creates new immutable VInt.
     */
    public static VInt newVInt(final Integer value, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVInt(value, alarmSeverity, alarmStatus, timeStamp, timeUserTag, true, lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    /**
     * Creates new immutable VDouble.
     */
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
     * Creates new immutable VDouble by using metadata from the old value,
     * now as timestamp and computing alarm from the metadata range.
     * 
     * @param value new numeric value
     * @param display metadata
     * @return new value
     */
    public static VDouble newVDouble(double value, Display display) {
        return newVDouble(value, TimeStamp.now(), display);
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
                alarmStatus, timeStamp, timeUserTag, true, lowerDisplayLimit, lowerCtrlLimit,
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
        return new IVInt(value, alarmSeverity, alarmStatus, timeStamp, timeUserTag, true, lowerDisplayLimit,
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

    public static VDoubleArray newVDoubleArray(final double[] values, final List<Integer> sizes, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVDoubleArray(values, sizes, alarmSeverity, alarmStatus, timeStamp, timeUserTag, true,
                lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat,
                upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    public static VImage newVImage(int height, int width, byte[] data) {
        return new IVImage(height, width, data);
    }

    static VIntArray newVIntArray(final int[] values, final List<Integer> sizes, final AlarmSeverity alarmSeverity,
            final AlarmStatus alarmStatus, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new IVIntArray(values, sizes, alarmSeverity, alarmStatus, timeStamp, timeUserTag, true,
                lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit, units, numberFormat,
                upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

}
