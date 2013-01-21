/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import org.epics.util.time.Timestamp;


/**
 * Partial implementation for numeric types.
 *
 * @author carcassi
 */
class IVMetadata implements Alarm, Time {
    
    private final Alarm alarm;
    private final Time time;

    public IVMetadata(Alarm alarm, Time time) {
        this.alarm = alarm;
        this.time = time;
    }

    @Override
    public AlarmSeverity getAlarmSeverity() {
        return alarm.getAlarmSeverity();
    }

    @Override
    public String getAlarmName() {
        return alarm.getAlarmName();
    }

    @Override
    public Timestamp getTimestamp() {
        return time.getTimestamp();
    }

    @Override
    public Integer getTimeUserTag() {
        return time.getTimeUserTag();
    }

    @Override
    public boolean isTimeValid() {
        return time.isTimeValid();
    }

}
