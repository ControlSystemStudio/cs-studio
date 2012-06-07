/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.text.NumberFormat;

/**
 * Allows to build a Display object which can then be re-used to generate
 * values with the same metadata.
 *
 * @author carcassi
 */
public class DisplayBuilder {
    
    private Double lowerDisplayLimit;
    private Double lowerCtrlLimit;
    private Double lowerAlarmLimit;
    private Double lowerWarningLimit;
    private String units;
    private NumberFormat format;
    private Double upperWarningLimit;
    private Double upperAlarmLimit;
    private Double upperCtrlLimit;
    private Double upperDisplayLimit;

    DisplayBuilder(Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        this.lowerDisplayLimit = lowerDisplayLimit;
        this.lowerCtrlLimit = lowerCtrlLimit;
        this.lowerAlarmLimit = lowerAlarmLimit;
        this.lowerWarningLimit = lowerWarningLimit;
        this.units = units;
        this.format = format;
        this.upperWarningLimit = upperWarningLimit;
        this.upperAlarmLimit = upperAlarmLimit;
        this.upperCtrlLimit = upperCtrlLimit;
        this.upperDisplayLimit = upperDisplayLimit;
    }
    
    public DisplayBuilder lowerDisplayLimit(Double lowerDisplayLimit) {
        this.lowerDisplayLimit = lowerDisplayLimit;
        return this;
    }
    
    public DisplayBuilder lowerCtrlLimit(Double lowerCtrlLimit) {
        this.lowerCtrlLimit = lowerCtrlLimit;
        return this;
    }
    
    public DisplayBuilder lowerAlarmLimit(Double lowerAlarmLimit) {
        this.lowerAlarmLimit = lowerAlarmLimit;
        return this;
    }
    
    public DisplayBuilder lowerWarningLimit(Double lowerWarningLimit) {
        this.lowerWarningLimit = lowerWarningLimit;
        return this;
    }
    
    public DisplayBuilder upperWarningLimit(Double upperWarningLimit) {
        this.upperWarningLimit = upperWarningLimit;
        return this;
    }
    
    public DisplayBuilder upperAlarmLimit(Double upperAlarmLimit) {
        this.upperAlarmLimit = upperAlarmLimit;
        return this;
    }
    
    public DisplayBuilder upperCtrlLimit(Double upperCtrlLimit) {
        this.upperCtrlLimit = upperCtrlLimit;
        return this;
    }
    
    public DisplayBuilder upperDisplayLimit(Double upperDisplayLimit) {
        this.upperDisplayLimit = upperDisplayLimit;
        return this;
    }
    
    public DisplayBuilder units(String units) {
        this.units = units;
        return this;
    }
    
    public DisplayBuilder format(NumberFormat format) {
        this.format = format;
        return this;
    }
    
    public Display build() {
        return ValueFactory.newDisplay(lowerDisplayLimit,
                lowerAlarmLimit, lowerWarningLimit, units, format, upperWarningLimit,
                upperAlarmLimit, upperDisplayLimit, lowerCtrlLimit, upperCtrlLimit);
    }

}
