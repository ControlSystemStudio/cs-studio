/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import javax.xml.bind.annotation.XmlAttribute;
import org.epics.pvmanager.util.TimeStamp;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.Time;

/**
 *
 * @author carcassi
 */
class XmlVMetaData extends ReplayValue implements Time, Alarm {

    @XmlAttribute
    Integer timeUserTag;
    @XmlAttribute
    AlarmSeverity alarmSeverity;
    @XmlAttribute
    AlarmStatus alarmStatus;

    @Override
    public Integer getTimeUserTag() {
        return timeUserTag;
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
    public boolean isTimeValid() {
        return true;
    }

    @Override
    public TimeStamp getTimeStamp() {
        return TimeStamp.timestampOf(getTimestamp());
    }

}
