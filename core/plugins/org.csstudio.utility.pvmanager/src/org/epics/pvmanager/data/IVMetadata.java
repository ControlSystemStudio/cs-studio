/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.pvmanager.util.TimeStamp;
import org.epics.util.time.Timestamp;


/**
 * Partial implementation for numeric types.
 *
 * @author carcassi
 */
class IVMetadata implements Alarm, Time {
    
    private final AlarmSeverity alarmSeverity;
    private final AlarmStatus alarmStatus;
    private final Timestamp timestamp;
    private final Integer timeUserTag;
    private final boolean timeValid;

    IVMetadata(AlarmSeverity alarmSeverity, AlarmStatus alarmStatus,
            Timestamp timestamp, Integer timeUserTag, boolean timeValid) {
        this.alarmSeverity = alarmSeverity;
        this.alarmStatus = alarmStatus;
        this.timestamp = timestamp;
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
        return TimeStamp.timestampOf(timestamp);
    }

    @Override
    public Timestamp getTimestamp() {
        return timestamp;
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
