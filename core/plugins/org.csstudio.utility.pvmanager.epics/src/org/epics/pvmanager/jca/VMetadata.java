/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import gov.aps.jca.dbr.TIME;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Time;
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
        return DataUtils.isTimeValid(dbrValue.getTimeStamp());
    }

}
