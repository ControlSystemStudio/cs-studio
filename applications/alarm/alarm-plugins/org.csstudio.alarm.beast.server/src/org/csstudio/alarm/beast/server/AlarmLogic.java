/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.csstudio.alarm.beast.server.Activator.logger;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.alarm.beast.SeverityLevel;

/** Alarm handling logic.
 *  <p>
 *  Maintains the alarm state, which consists of:
 *  <ul>
 *  <li>Severity and message of the PV as far as the alarm system is
 *      concerned. This might be the latched, highest un-acknowledged
 *      severity/message. It might also be an acknowledged severity level.
 *  <li>"Current" severity of the PV. This severity may be lower than
 *      the latched alarm system severity.
 *  <li>Timestamp and Value that caused the Severity and message
 *  </ul>
 *  <p>
 *  Logic can 'latch' to the highest un-acknowledged alarm state
 *  and trigger annunciation when new alarm becomes active.
 *  <p>
 *  Abstract base of AlarmPV to allow tests independent from actual
 *  control system connection.
 *
 *  @see AlarmPV
 *  @see AlarmLogicUnitTest
 *  @author Kay Kasemir
 */
public class AlarmLogic implements DelayedAlarmListener, GlobalAlarmListener
{
    /** @see #getMaintenanceMode() */
    private static volatile boolean maintenance_mode = false;

    /** Listener to notify on alarm state changes */
    final private AlarmLogicListener listener;

    /** Is logic enabled, or only following the 'current' PV state
     *  without actually alarming?
     */
    private final AtomicBoolean enabled = new AtomicBoolean(true);

    /** 'Current' state that was received while disabled.
     *  Is cached in case we get re-enabled, whereupon it is used
     */
    private volatile AlarmState disabled_state = null;

    /** Latch the highest received alarm severity/status?
     *  When <code>false</code>, the latched alarm state actually
     *  follows the current value of the PV without requiring
     *  acknowledgment.
     */
    private boolean latching;

    /** Annunciate alarms? */
    private volatile boolean annunciating;

    /** Does this alarm have priority in maintenance mode, i.e.
     *  INVALID should still be annunciated in maintenance mode,
     *  and the annunciator will not suppress it within a flurry of
     *  alarms that are usually throttled/summarized
     */
    private boolean has_priority = false;

    /** Require minimum time [seconds] in alarm before indicating alarm */
    private volatile int delay;

    /** Pending delayed alarm state update or <code>null</code> */
    final private DelayedAlarmUpdate delayed_check = new DelayedAlarmUpdate(this);

    /** Alarm when PV != OK more often than this count within delay */
    private AlarmStateHistory alarm_history = null;

    /** Current state of the control system channel */
    private AlarmState current_state;

    /** Alarm logic state, with might be latched or delayed from current_state */
    private AlarmState alarm_state;

    /** Delay [seconds] after which a 'global' alarm state update is sent */
    final private int global_delay;

    /** Pending global alarm state update or <code>null</code> */
    final private GlobalAlarmUpdate global_check = new GlobalAlarmUpdate(this);

    /** Is there an active 'global' alarm, i.e. a global alarm notification
     *  has been sent?
     */
    private boolean in_global_alarm = false;

    /** Initialize
     *  @param listener {@link AlarmLogicListener}
     *  @param latching Latch the highest received alarm severity?
     *  @param annunciating Annunciate alarms?
     *  @param delay Minimum time in alarm before indicating alarm [seconds]
     *  @param count Alarm when PV != OK more often than this count within delay
     *  @param current_state Current alarm state of PV
     *  @param alarm_state Alarm logic state
     *  @param global_delay Delay [seconds] after which a 'global' notification is sent. 0 to disable
     */
    public AlarmLogic(final AlarmLogicListener listener,
            final boolean latching, final boolean annunciating,
            final int delay,
            final int count,
            final AlarmState current_state,
            final AlarmState alarm_state,
            final int global_delay)
    {
        this.listener = listener;
        this.latching = latching;
        this.annunciating = annunciating;
        this.delay = delay;
        if (count > 0)
            alarm_history = new AlarmStateHistory(count);
        this.current_state = current_state;
        this.alarm_state = alarm_state;
        this.global_delay = global_delay;
    }

    /** Set maintenance mode.
     *  @param maintenance_mode
     *  @see #getMaintenanceMode()
     */
    @SuppressWarnings("nls")
    public static void setMaintenanceMode(final boolean maintenance_mode)
    {
        AlarmLogic.maintenance_mode = maintenance_mode;
        logger.config("Maintenance Mode: " + maintenance_mode);
    }

    /** In maintenance mode, 'INVALID' alarms are suppressed by
     *  _not_ annunciating them, automatically acknowledging them
     *  and otherwise treating them like 'OK':
     *  When INVALID_ACK is followed by 'MAJOR', that is considered
     *  a new alarm since the INVALID didn't really count.
     *  @return <code>true</code> in maintenance mode
     */
    public static boolean getMaintenanceMode()
    {
        return maintenance_mode;
    }

    /** @param enable Enable or disable the logic? */
    public void setEnabled(final boolean enable)
    {
        // Ignore if there's no change
        if (enabled.getAndSet(enable) == enable)
            return;
        listener.alarmEnablementChanged(enable);
        if (!enable)
        {   // Disabled
            final AlarmState current, alarm;
            synchronized (this)
            {   // Remember current PV state in case we're re-enabled
                disabled_state = current_state;
                // Otherwise pretend all is OK, using special message
                current_state = AlarmState.createClearState(current_state.getValue());
                alarm_state = new AlarmState(SeverityLevel.OK,
                        Messages.AlarmMessageDisabled, "", //$NON-NLS-1$
                        Instant.now());
                current = current_state;
                alarm = alarm_state;
            }
            listener.alarmStateChanged(current, alarm);
        }
        else
        {   // (Re-)enabled
            if (disabled_state != null)
            {
                computeNewState(disabled_state);
                disabled_state = null;
            }
        }
    }

    /** @return Are alarms enabled */
    public boolean isEnabled()
    {
        return enabled.get();
    }

    /** @param annunciating <code>true</code> to annunciate */
    public void setAnnunciate(final boolean annunciating)
    {
        this.annunciating = annunciating;
    }

    /** @return <code>true</code> if configured to annunciate */
    public boolean isAnnunciating()
    {
        return annunciating;
    }

    /** @param has_priority Does this alarm have priority in maintenance mode? */
    synchronized public void setPriority(final boolean has_priority)
    {
        this.has_priority = has_priority;
    }

    /** @param latching <code>true</code> for latching behavior */
    synchronized public void setLatching(boolean latching)
    {
        this.latching = latching;
    }

    /** @return <code>true</code> for latching behavior */
    synchronized public boolean isLatching()
    {
        return latching;
    }

    /** @param seconds Alarm delay in seconds */
    public void setDelay(final int seconds)
    {
        delay = seconds;
    }

    /** @return Alarm delay in seconds */
    public int getDelay()
    {
        return delay;
    }

    /** @return count Alarm when PV != OK more often than this count within delay */
    synchronized protected int getCount()
    {
        if (alarm_history == null)
            return 0;
        return alarm_history.getCount();
    }

    /** Alarm when PV != OK more often than this count within delay
     *  @param count New count
     */
    synchronized protected void setCount(final int count)
    {
        if (alarm_history != null)
            alarm_history.dispose();
        if (count > 0)
            alarm_history = new AlarmStateHistory(count);
        else
            alarm_history = null;
    }

    /** @return Current state of PV */
    synchronized public AlarmState getCurrentState()
    {
        return current_state;
    }

    /** @return Alarm system state */
    synchronized public AlarmState getAlarmState()
    {
        return alarm_state;
    }

    /** Compute the new alarm state based on new data from control system.
     *  @param received_state Severity/Status received from control system
     */
    public void computeNewState(final AlarmState received_state)
    {
        final boolean return_to_ok;
        synchronized (this)
        {
            // When disabled, ignore...
            if (!enabled.get())
            {
                disabled_state = received_state;
                return;
            }
            // Remember what used to be the 'current' severity
            final SeverityLevel previous_severity = current_state.getSeverity();
            final String previous_message = current_state.getMessage();
            // Update current severity.
            current_state = received_state;
            // If there's no change to the current severity and message, we're done.
            if (received_state.getSeverity() == previous_severity  &&
                received_state.getMessage().equals(previous_message))
                return;

            // Does this 'clear' an acknowledged severity? -> OK
            final boolean alarm_cleared =
                   (current_state.getSeverity() == SeverityLevel.OK  &&
                    !alarm_state.getSeverity().isActive());
            // Are we in maintenance mode, and this is a new
            // alarm after an INVALID one that was suppressed?
            // -> At least briefly return to OK so that 'new' alarm
            //    will take effect
            final boolean maint_leaving_invalid =
             maintenance_mode &&
             // Current severity is better than INVALID & UNDEFINED
             current_state.getSeverity().ordinal() < SeverityLevel.INVALID.ordinal()  &&
             // Alarm state was INVALID, INVALID_ACK, UNDEFINED, UNDEFINED_ACK
             alarm_state.getSeverity().getAlarmUpdatePriority()
                          >= SeverityLevel.INVALID.getAlarmUpdatePriority();

            return_to_ok = alarm_cleared  ||  maint_leaving_invalid;
            if (return_to_ok)
                alarm_state = AlarmState.createClearState(received_state.getValue(), received_state.getTime());
        }
        // Out of sync'ed section
        if (return_to_ok)
        {
            // If a delayed alarm timer was started, cancel it
            delayed_check.cancel();
            // Also cancel 'global' timers
            global_check.cancel();
            clearGlobalAlarm();
        }
        updateState(received_state, getDelay() > 0);
    }

    /** Compute the new alarm state based on current state and new data from
     *  control system.
     *
     *  @param received_state Severity/Status received from control system,
     *                                         or
     *                        the delayed new state from DelayedAlarmCheck
     *  @param with_delay Use delay when raising the alarm severity?
     */
    private void updateState(final AlarmState received_state,
                             final boolean with_delay)
    {
        SeverityLevel raised_level = null;
        final AlarmState current, alarm;
        synchronized (this)
        {
            // Update alarm state. If there is already an update pending,
            // update that delayed state check.
            AlarmState state_to_update = delayed_check.getState();
            if (state_to_update == null)
                state_to_update = alarm_state;
            final AlarmState new_state = latchAlarmState(state_to_update, received_state);
            // Computed a new alarm state? Else: Only current_severity update
            if (new_state != null)
            {
                // Delay if requested and this is indeed triggered by alarm, not OK
                if (with_delay && received_state.getSeverity() != SeverityLevel.OK)
                {   // Start or update delayed alarm check
                    delayed_check.schedule_update(new_state, delay);
                    // Somewhat in parallel, check for alarm counts
                    if (checkCount(received_state))
                    {   // Exceeded alarm count threshold.
                        // Reset delayed alarms
                        delayed_check.cancel();
                        // Annunciate if going to higher alarm severity
                        if (new_state.hasHigherUpdatePriority(alarm_state))
                            raised_level = new_state.getSeverity();
                        alarm_state = new_state;
                    }
                }
                else
                {   // Annunciate if going to higher alarm severity
                    if (new_state.hasHigherUpdatePriority(alarm_state))
                        raised_level = new_state.getSeverity();
                    alarm_state = new_state;
                }
            }
            // In maint. mode, INVALID & UNDEFINED are automatically ack'ed and not annunciated,
            // except for 'priority' alarms
            if (maintenance_mode &&
                !has_priority &&
                alarm_state.getSeverity().ordinal() >= SeverityLevel.INVALID.ordinal())
            {
                alarm_state = alarm_state.createAcknowledged(alarm_state);
                raised_level = null;
            }
            current = current_state;
            alarm = alarm_state;
        }
        // Update state
        listener.alarmStateChanged(current, alarm);
        // New, higher alarm level?
        if (raised_level != null)
        {
            if (isAnnunciating())
                listener.annunciateAlarm(raised_level);
            if (global_delay > 0)
                global_check.schedule_update(global_delay);
        }
    }

    /** {@inheritDoc}
     *  @see DelayedAlarmListener
     */
    @Override
    public void delayedStateUpdate(final AlarmState delayed_state)
    {
        updateState(delayed_state, false);
    }

    /** Check if the new state adds up to 'count' alarms within 'delay'
     *  @param received_state
     *  @return <code>true</code> if alarm count reached/exceeded
     */
    private boolean checkCount(final AlarmState received_state)
    {
        if (alarm_history == null)
            return false;
        alarm_history.add(received_state);
        if (! alarm_history.receivedAlarmsWithinTimerange(delay))
            return false;
        // Exceeded the alarm count. Reset the counter for next time.
        alarm_history.reset();
        return true;
    }

    /** Determine new alarm state based on severities and latching behavior
     *  @param state_to_update Alarm state to update
     *  @param received_state Alarm state received from control system
     *  @return New alarm state or <code>null</code> if current state unchanged
     */
    private AlarmState latchAlarmState(final AlarmState state_to_update,
            final AlarmState received_state)
    {
        if (latching)
        {   // Latch to maximum severity
            if (received_state.hasHigherUpdatePriority(state_to_update))
                return received_state;
        }
        else
        {   // Not 'latched': Follow _active_ alarms
            if (state_to_update.getSeverity().isActive())
                return received_state;
            else
            {   // We have an acknowledged severity.
                // Keep unless received state is higher
                if (received_state.hasHigherUpdatePriority(state_to_update))
                      return received_state;
            }
        }
        // No change
        return null;
    }

    /** Send 'global' alarm update.
     *  {@inheritDoc}
     *  @see GlobalAlarmListener
     */
    @Override
    public void updateGlobalState()
    {
        final AlarmState state;
        synchronized (this)
        {
            in_global_alarm = true;
            state = alarm_state;
        }
        listener.globalStateChanged(state);
    }

    /** If there was a 'global' alarm, clear it and notify listener */
    private void clearGlobalAlarm()
    {
        final AlarmState state;
        synchronized (this)
        {
            if (!in_global_alarm)
                return;
            in_global_alarm = false;
            state = alarm_state;
        }
        listener.globalStateChanged(state);
    }

    /** Acknowledge current alarm severity
     *  @param acknowledge Acknowledge or un-acknowledge?
     */
    public void acknowledge(boolean acknowledge)
    {
        final AlarmState current, alarm;
        // Cancel any scheduled 'global' update
        global_check.cancel();
        boolean clear_global_alarm = false;
        synchronized (this)
        {
            if (acknowledge)
            {   // Does this actually 'clear' an acknowledged severity?
                if (current_state.getSeverity() == SeverityLevel.OK)
                {
                    alarm_state = AlarmState.createClearState(current_state.getValue());
                    clear_global_alarm = true;
                }
                else
                    alarm_state = alarm_state.createAcknowledged(current_state);
            }
            else
                alarm_state = alarm_state.createUnacknowledged();
            current = current_state;
            alarm = alarm_state;
        }
        // Notify listeners of latest state
        if (clear_global_alarm)
            clearGlobalAlarm();
        listener.alarmStateChanged(current, alarm);
    }

    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Current: " + current_state + ", Alarm: " + alarm_state;
    }
}
