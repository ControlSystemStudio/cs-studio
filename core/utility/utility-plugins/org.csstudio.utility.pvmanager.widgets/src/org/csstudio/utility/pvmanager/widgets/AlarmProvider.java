package org.csstudio.utility.pvmanager.widgets;

import org.epics.vtype.Alarm;

/**
 * A widget that provides an alarm to be displayed as a border.
 *
 * @author Gabriele Carcassi
 */
public interface AlarmProvider {

    /**
     * Returns the alarm that should be displayed as a border.
     *
     * @return the alarm; never null
     */
    public Alarm getAlarm();

}
