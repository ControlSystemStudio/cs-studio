/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.AbstractList;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.TimestampFormat;
import org.epics.vtype.table.VTableFactory;

/**
 * Helper class that provides default implementation of toString for VTypes.
 *
 * @author carcassi
 */
public class VTypeToString {
    private VTypeToString() {
        // Do not create
    }
    
    private static void appendAlarm(StringBuilder builder, Alarm alarm) {
        if (!alarm.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
            builder.append(", ")
                    .append(alarm.getAlarmSeverity())
                    .append("(")
                    .append(alarm.getAlarmName())
                    .append(")");
        }
    }
    
    /**
     * Converts the given alarm to a string.
     * 
     * @param alarm the alarm
     * @return the string representation; never null
     */
    public static String alarmToString(Alarm alarm) {
        if (alarm == null) {
            return "NONE";
        }
        
        return alarm.getAlarmSeverity() + "(" + alarm.getAlarmName() + ")";
    }

    /**
     * Converts the given time to a string.
     * 
     * @param time the time
     * @return the string representation; never null
     */
    public static String timeToString(Time time) {
        if (time == null) {
            return "null";
        }
        
        return timeFormat.format(time.getTimestamp()) + "(" + time.getTimeUserTag()+ ")";
    }
    
    private static final TimestampFormat timeFormat = new TimestampFormat("yyyy/MM/dd hh:mm:ss.SSS");
    
    private static void appendTime(StringBuilder builder, Time time) {
        builder.append(", ").append(timeFormat.format(time.getTimestamp()));
    }
    
    /**
     * Default toString implementation for VNumber.
     *
     * @param vNumber the object
     * @return the string representation
     */
    public static String toString(VNumber vNumber) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vNumber);
        builder.append(type.getSimpleName())
                .append('[')
                .append(vNumber.getValue());
        appendAlarm(builder, vNumber);
        appendTime(builder, vNumber);
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * Default toString implementation for VString.
     *
     * @param vString the object
     * @return the string representation
     */
    public static String toString(VString vString) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vString);
        builder.append(type.getSimpleName())
                .append("[")
                .append(vString.getValue());
        appendAlarm(builder, vString);
        appendTime(builder, vString);
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * Default toString implementation for VBoolean.
     *
     * @param vBoolean the object
     * @return the string representation
     */
    public static String toString(VBoolean vBoolean) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vBoolean);
        builder.append(type.getSimpleName())
                .append("[")
                .append(vBoolean.getValue());
        appendAlarm(builder, vBoolean);
        appendTime(builder, vBoolean);
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * Default toString implementation for VEnum.
     *
     * @param vEnum the object
     * @return the string representation
     */
    public static String toString(VEnum vEnum) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vEnum);
        builder.append(type.getSimpleName())
                .append("[")
                .append(vEnum.getValue())
                .append("(")
                .append(vEnum.getIndex())
                .append(")");
        appendAlarm(builder, vEnum);
        appendTime(builder, vEnum);
        builder.append(']');
        return builder.toString();
    }
    
    private final static ValueFormat format = new SimpleValueFormat(3);
    
    static {
        format.setNumberFormat(NumberFormats.toStringFormat());
    }
    
    /**
     * Default toString implementation for VNumberArray.
     *
     * @param vNumberArray the object
     * @return the string representation
     */
    public static String toString(VNumberArray vNumberArray) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vNumberArray);
        builder.append(type.getSimpleName())
                .append("[");
        builder.append(format.format(vNumberArray));
        builder.append(", size ")
                .append(vNumberArray.getData().size());
        appendAlarm(builder, vNumberArray);
        appendTime(builder, vNumberArray);
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * Default toString implementation for VStringArray.
     *
     * @param vStringArray the object
     * @return the string representation
     */
    public static String toString(VStringArray vStringArray) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vStringArray);
        builder.append(type.getSimpleName())
                .append("[");
        builder.append(format.format(vStringArray));
        builder.append(", size ")
                .append(vStringArray.getData().size());
        appendAlarm(builder, vStringArray);
        appendTime(builder, vStringArray);
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * Default toString implementation for VEnumArray.
     *
     * @param vEnumArray the object
     * @return the string representation
     */
    public static String toString(VEnumArray vEnumArray) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vEnumArray);
        builder.append(type.getSimpleName())
                .append("[");
        builder.append(format.format(vEnumArray));
        builder.append(", size ")
                .append(vEnumArray.getData().size());
        appendAlarm(builder, vEnumArray);
        appendTime(builder, vEnumArray);
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * Default toString implementation for VTable.
     *
     * @param vTable the object
     * @return the string representation
     */
    public static String toString(final VTable vTable) {
        StringBuilder builder = new StringBuilder();
        builder.append("VTable")
                .append("[")
                .append(vTable.getColumnCount())
                .append("x")
                .append(vTable.getRowCount())
                .append(", ");
        builder.append(format.format(ValueFactory.newVStringArray(VTableFactory.columnNames(vTable), ValueFactory.alarmNone(), ValueFactory.timeNow())));
        builder.append(']');
        return builder.toString();
    }
}
