/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.data;

/**
 * Helper class that provides default implementation of toString for VTypes.
 *
 * @author carcassi
 */
public class VTypeToString {
    private VTypeToString() {
        // Do not create
    }
    
    public static String toString(VNumber vNumber) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vNumber);
        builder.append(type.getSimpleName())
                .append('[')
                .append(vNumber.getValue());
        if (!vNumber.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
            builder.append(", ")
                    .append(vNumber.getAlarmSeverity())
                    .append("(")
                    .append(vNumber.getAlarmName())
                    .append(")");
        }
        builder.append(", ")
                .append(vNumber.getTimestamp())
                .append(']');
        return builder.toString();
    }
    
    public static String toString(VString vString) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vString);
        builder.append(type.getSimpleName())
                .append("[\"")
                .append(vString.getValue())
                .append('\"');
        if (!vString.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
            builder.append(", ")
                    .append(vString.getAlarmSeverity())
                    .append("(")
                    .append(vString.getAlarmName())
                    .append(")");
        }
        builder.append(", ")
                .append(vString.getTimestamp())
                .append(']');
        return builder.toString();
    }
    
    public static String toString(VEnum vEnum) {
        StringBuilder builder = new StringBuilder();
        Class type = ValueUtil.typeOf(vEnum);
        builder.append(type.getSimpleName())
                .append("[\"")
                .append(vEnum.getValue())
                .append("\"(")
                .append(vEnum.getIndex())
                .append(")");
        if (!vEnum.getAlarmSeverity().equals(AlarmSeverity.NONE)) {
            builder.append(", ")
                    .append(vEnum.getAlarmSeverity())
                    .append("(")
                    .append(vEnum.getAlarmName())
                    .append(")");
        }
        builder.append(", ")
                .append(vEnum.getTimestamp())
                .append(']');
        return builder.toString();
    }
}
