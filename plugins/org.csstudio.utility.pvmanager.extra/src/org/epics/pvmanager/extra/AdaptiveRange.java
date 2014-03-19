/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import java.text.NumberFormat;
import java.util.List;
import org.epics.vtype.Display;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VNumberArray;
import org.epics.util.array.ListNumber;

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
    private boolean limitsChanged = false;

    public boolean limitsChanged() {
        boolean returnValue = limitsChanged;
        limitsChanged = false;
        return returnValue;
    }

    public void considerValues(List<? extends VNumberArray> values) {
        for (VNumberArray vDoubleArray : values) {
            considerValues(vDoubleArray.getData());
        }
    }

    void considerValues(ListNumber array) {
        for (int i = 0; i < array.size(); i++) {
            double d = array.getDouble(i);
            if (!Double.isNaN(d)) {
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
                    limitsChanged = true;
                } else {
                    if (d > upperDisplayLimit) {
                        upperWarningLimit = d;
                        upperAlarmLimit = d;
                        upperCtrlLimit = d;
                        upperDisplayLimit = d;
                        limitsChanged = true;
                    }
                    if (d < lowerDisplayLimit) {
                        lowerDisplayLimit = d;
                        lowerCtrlLimit = d;
                        lowerAlarmLimit = d;
                        lowerWarningLimit = d;
                        limitsChanged = true;
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
