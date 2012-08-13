/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.util.time.Timestamp;

/**
 * Converts numeric types to VDouble.
 *
 * @author carcassi
 */
@Deprecated
class ConverterVDoubleFunction extends Function<VDouble> {
    
    private final Function<?> argument;

    /**
     * Creates a new converter from the given function.
     * 
     * @param argument the argument function
     */
    public ConverterVDoubleFunction(Function<?> argument) {
        this.argument = argument;
    }

    @Override
    public VDouble getValue() {
        Object value = argument.getValue();
        if (value instanceof VDouble) {
            return (VDouble) value;
        }
        
        // Convert VInt to VDouble
        if (value instanceof VInt) {
            final VInt vInt = (VInt) value;
            return new VDouble() {

                @Override
                public Double getValue() {
                    return vInt.getValue().doubleValue();
                }

                @Override
                public AlarmSeverity getAlarmSeverity() {
                    return vInt.getAlarmSeverity();
                }

                @Override
                public AlarmStatus getAlarmStatus() {
                    return vInt.getAlarmStatus();
                }

                @Override
                public TimeStamp getTimeStamp() {
                    return vInt.getTimeStamp();
                }

                @Override
                public Timestamp getTimestamp() {
                    return vInt.getTimestamp();
                }

                @Override
                public Integer getTimeUserTag() {
                    return vInt.getTimeUserTag();
                }

                @Override
                public boolean isTimeValid() {
                    return vInt.isTimeValid();
                }

                @Override
                public Double getLowerDisplayLimit() {
                    return vInt.getLowerDisplayLimit();
                }

                @Override
                public Double getLowerCtrlLimit() {
                    return vInt.getLowerCtrlLimit();
                }

                @Override
                public Double getLowerAlarmLimit() {
                    return vInt.getLowerAlarmLimit();
                }

                @Override
                public Double getLowerWarningLimit() {
                    return vInt.getLowerWarningLimit();
                }

                @Override
                public String getUnits() {
                    return vInt.getUnits();
                }

                @Override
                public NumberFormat getFormat() {
                    return vInt.getFormat();
                }

                @Override
                public Double getUpperWarningLimit() {
                    return vInt.getUpperWarningLimit();
                }

                @Override
                public Double getUpperAlarmLimit() {
                    return vInt.getUpperAlarmLimit();
                }

                @Override
                public Double getUpperCtrlLimit() {
                    return vInt.getUpperCtrlLimit();
                }

                @Override
                public Double getUpperDisplayLimit() {
                    return vInt.getUpperDisplayLimit();
                }
            };
        }
        
        // No convertion available
        throw new UnsupportedOperationException("Cannot convert a " + value.getClass().getSimpleName() + " to a VDouble.");
    }
    
}
