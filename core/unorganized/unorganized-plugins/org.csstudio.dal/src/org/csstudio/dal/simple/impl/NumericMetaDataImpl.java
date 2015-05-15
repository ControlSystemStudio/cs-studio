package org.csstudio.dal.simple.impl;

import org.csstudio.dal.AccessType;
import org.csstudio.dal.simple.MetaData;

@Deprecated
public final class NumericMetaDataImpl implements MetaData {

    private final double alarmHigh;
    private final double alarmLow;
    private final double displayHigh;
    private final double displayLow;
    private final double warnHigh;
    private final double warnLow;
    private final int precision;
    private final String units;

    public NumericMetaDataImpl(double displayLow, double displayHigh, double warnLow, double warnHigh, double alarmLow, double alarmHigh, int precision, String units) {
        this.alarmHigh = alarmHigh;
        this.alarmLow = alarmLow;
        this.displayHigh = displayHigh;
        this.displayLow = displayLow;
        this.warnHigh = warnHigh;
        this.warnLow = warnLow;
        this.precision = precision;
        this.units = units;
    }

    public double getAlarmHigh() {
        return alarmHigh;
    }

    public double getAlarmLow() {
        return alarmLow;
    }

    public double getDisplayHigh() {
        return displayHigh;
    }

    public double getDisplayLow() {
        return displayLow;
    }

    public int getPrecision() {
        return precision;
    }

    public String getState(int state) {
        // default value
        return null;
    }

    public String[] getStates() {
        return new String[0];
    }

    public Object getStateValue(int state) {
        //default value
        return null;
    }

    public Object[] getStateValues() {
        return new Object[0];
    }


    public String getUnits() {
        return units;
    }

    public double getWarnHigh() {
        return warnHigh;
    }

    public double getWarnLow() {
        return warnLow;
    }

    public AccessType getAccessType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDataType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getHostname() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSequenceLength() {
        // TODO Auto-generated method stub
        return 0;
    }

}
