/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.pvmanager.util.TimeStamp;

/**
 * Partial implementation for numeric types.
 *
 * @author carcassi
 */
class IVMetadata implements Alarm, Time {
    
    private final AlarmSeverity alarmSeverity;
    private final AlarmStatus alarmStatus;
    private final TimeStamp timeStamp;
    private final Integer timeUserTag;
    private final boolean timeValid;

    IVMetadata(AlarmSeverity alarmSeverity, AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, boolean timeValid) {
        this.alarmSeverity = alarmSeverity;
        this.alarmStatus = alarmStatus;
        this.timeStamp = timeStamp;
        this.timeUserTag = timeUserTag;
        this.timeValid = timeValid;
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
    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Integer getTimeUserTag() {
        return timeUserTag;
    }

    @Override
    public boolean isTimeValid() {
        return timeValid;
    }

}
