/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.util.List;
import java.util.Set;

/**
 * Alarm information.
 * <p>
 * The alarm status is represented by a set of status bits that could be either
 * set or unset. This is implemented in Java by the use of Sets.
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
     * Returns the set of alarm statuses that are currently active. Never null.
     *
     * @return a set of enabled alarms
     */
    Set<String> getAlarmStatus();
    
    /**
     * Defines all possible alarm statuses that are valid on this channel. Never null;
     * if not connected returns an empty list. In Epics 3, this list is going
     * to be the same for all PVs. In Epics V, this list is going to be possibly
     * different for each channel, but the common lists for client/server
     * pairs should be cached.
     * 
     * @return a set of labels
     */
    @Metadata
    List<String> getPossibleAlarms();
}
