/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

/**
 * Alarm information. Represents the severity and status of the highest alarm
 * associated with the channel.
 *
 * @author carcassi
 */
public interface Alarm {

    /**
     * Returns the alarm severity, which describes the quality of the
     * value returned. Never null.
     *
     * @return the alarm severity
     */
    AlarmSeverity getAlarmSeverity();
    
    /**
     * Returns a brief text representation of the highest currently active alarm.
     * Never null.
     *
     * @return the alarm status
     */
    String getAlarmName();
    
    /**
     * Returns the alarm status of the highest currently active alarm.
     * Never null.
     *
     * @deprecated use of AlarmStatus is being deprecated in favor of a simple String
     * @return the alarm status
     */
    @Deprecated
    AlarmStatus getAlarmStatus();
}
