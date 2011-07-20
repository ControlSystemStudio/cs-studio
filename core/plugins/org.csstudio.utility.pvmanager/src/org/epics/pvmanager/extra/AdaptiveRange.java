/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.extra;

import java.text.NumberFormat;
import java.util.List;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VDoubleArray;

/**
 *
 * @author carcassi
 */
class AdaptiveRange implements Display {

    private Double lowerDisplayLimit = 0.0;
    private Double lowerCtrlLimit = 0.0;
    private Double lowerAlarmLimit = 0.0;
    private Double lowerWarningLimit = 0.0;
    private Double upperWarningLimit = 0.0;
    private Double upperAlarmLimit = 0.0;
    private Double upperCtrlLimit = 0.0;
    private Double upperDisplayLimit = 0.0;
    
    private boolean firstValue = true;

    public void considerValues(List<VDoubleArray> values) {
        for (VDoubleArray vDoubleArray : values) {
            for (int i = 0; i < vDoubleArray.getArray().length; i++) {
                double d = vDoubleArray.getArray()[i];
                if (firstValue) {
                    lowerDisplayLimit = d;
                    lowerCtrlLimit = d;
                    lowerAlarmLimit = d;
                    lowerWarningLimit = d;
                    upperWarningLimit = d;
                    upperAlarmLimit = d;
                    upperCtrlLimit = d;
                    upperDisplayLimit = d;
                    firstValue = false;
                } else {
                    if (d > upperDisplayLimit) {
                        upperWarningLimit = d;
                        upperAlarmLimit = d;
                        upperCtrlLimit = d;
                        upperDisplayLimit = d;
                    }
                    if (d < lowerDisplayLimit) {
                        lowerDisplayLimit = d;
                        lowerCtrlLimit = d;
                        lowerAlarmLimit = d;
                        lowerWarningLimit = d;
                    }
                }
            }
        }
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
        throw new UnsupportedOperationException("Units not part of auto range");
    }

    @Override
    public NumberFormat getFormat() {
        throw new UnsupportedOperationException("Format not part of auto range");
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
