/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.TIME;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.Time;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
class VMetadata<TValue extends TIME> implements Alarm, Time {

    final TValue dbrValue;
    private final boolean disconnected;

    VMetadata(TValue dbrValue, JCAConnectionPayload connPayload) {
        this.dbrValue = dbrValue;
        this.disconnected = !connPayload.isChannelConnected();
    }

    @Override
    public AlarmSeverity getAlarmSeverity() {
        if (disconnected)
            return AlarmSeverity.UNDEFINED;
        return DataUtils.fromEpics(dbrValue.getSeverity());
    }

    @Override
    public AlarmStatus getAlarmStatus() {
        if (disconnected)
            return AlarmStatus.CLIENT;
        return DataUtils.fromEpics(dbrValue.getStatus());
    }

    @Override
    public String getAlarmName() {
        if (disconnected)
            return "Disconnected";
        return dbrValue.getStatus().getName();
    }

    @Override
    public Timestamp getTimestamp() {
        return DataUtils.timestampOf(dbrValue.getTimeStamp());
    }

    @Override
    public Integer getTimeUserTag() {
        return null;
    }

    @Override
    public boolean isTimeValid() {
        return DataUtils.isTimeValid(getTimestamp());
    }

}
