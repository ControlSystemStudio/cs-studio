/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import org.epics.vtype.VStringArray;
import org.epics.vtype.Time;
import org.epics.vtype.VString;
import org.epics.vtype.VEnum;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueFormat;
import org.epics.vtype.SimpleValueFormat;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.ValueUtil;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.Alarm;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.TimestampFormat;

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
}
