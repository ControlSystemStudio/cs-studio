package org.csstudio.alarm.beast.server;

import org.csstudio.alarm.beast.SeverityLevel;

/** Listener to {@link AlarmLogic} updates
 *  @author Kay Kasemir
 */
public interface AlarmLogicListener
{
    /** Invoked when enablement changes.
     *  @param is_enabled Is alarm logic now enabled?
     */
    public void alarmEnablementChanged(boolean is_enabled);

    /** Invoked on change in alarm state, current or latched,
     *  to allow for notification of clients.
     *  @param current Current state of the input PV
     *  @param alarm Alarm state, which might be latched or delayed
     */
    public void alarmStateChanged(AlarmState current, AlarmState alarm);

    /** Invoked when annunciation is required.
     *  @param level Level to annunciate
     */
    public void annunciateAlarm(SeverityLevel level);

    /** Invoked when a 'global' alarm change happened:
     *  An alarm stayed un-acknowledged past the 'global' delay,
     *  or such a global alarm state cleared.
     *  @param alarm Global Alarm state
     */
    public void globalStateChanged(AlarmState alarm);
}
