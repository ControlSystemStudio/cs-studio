/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import org.epics.pvmanager.util.TimeStamp;

/**
 *
 * @author carcassi
 */
class IVString extends IVMetadata implements VString {

    private final String value;

    public IVString(String value,
            AlarmSeverity alarmSeverity, AlarmStatus alarmStatus,
            TimeStamp timeStamp, Integer timeUserTag, boolean timeValid) {
        super(alarmSeverity, alarmStatus, timeStamp, timeUserTag, timeValid);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
