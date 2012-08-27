/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
class IVString extends IVMetadata implements VString {

    private final String value;

    public IVString(String value,
            AlarmSeverity alarmSeverity, AlarmStatus alarmStatus,
            Timestamp timestamp, Integer timeUserTag, boolean timeValid) {
        super(alarmSeverity, alarmStatus, timestamp, timeUserTag, timeValid);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
