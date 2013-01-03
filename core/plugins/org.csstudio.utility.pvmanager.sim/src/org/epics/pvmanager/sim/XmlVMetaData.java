/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sim;

import javax.xml.bind.annotation.XmlAttribute;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;

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
    String alarmName;

    @Override
    public Integer getTimeUserTag() {
        return timeUserTag;
    }

    @Override
    public AlarmSeverity getAlarmSeverity() {
        return alarmSeverity;
    }

    @Override
    public String getAlarmName() {
        return alarmName;
    }

    @Override
    public boolean isTimeValid() {
        return true;
    }

}
