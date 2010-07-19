/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;

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
 *  @see AlarmLogicTest
 *  @author Kay Kasemir
 */
public abstract class AlarmLogic
{
    /** Timer for handling delayed alarms */
    private static Timer delay_timer = new Timer("Alarm Delay Timer", true); //$NON-NLS-1$
    
    /** Helper for checking alarms after a delay.
     *  Started with a new state, it will trigger a transition to that
     *  state only after a delay.
     *  While the timer is running, the state might be updated,
     *  for example to a higher latched state.
     *  Or the check gets canceled because the control system sent an 'OK'
     *  value in time.
     */
    class DelayedAlarmCheck extends TimerTask
    {
        private AlarmState state;
        
        /** Initialize
         *  @param new_state State to which we would go if there was no delay
         *  @param seconds Delay after which to re-evaluate
         */
        public DelayedAlarmCheck(final AlarmState new_state,
                                 final int seconds)
        {
            this.state = new_state;
            delay_timer.schedule(this, seconds * 1000L);
            //System.out.println("Update to " + new_state + " delayed for " + delay + " secs");
        }

        /** @return Alarm state to which we'll go after the delay expires */
        synchronized public AlarmState getState()
        {
            return state;
        }

        /** Update the alarm state handled by the delayed check because
         *  another value arrived from the control system
         *  @param new_state State to which we would go if there was no delay
         */
        synchronized public void update(final AlarmState new_state)
        {
            this.state = new_state;
            // System.out.println("DelayedAlarmCheck update to " + new_state);
        }

        /** Cancel the delayed alarm check because control system PV cleared
         *  @see java.util.TimerTask#cancel()
         */
        @Override
        public boolean cancel()
        {
            //System.out.println("Delayed alarm check canceled: " + state);
            // delayed_check is volatile, no need to synchronize?
            // Besides, this is called from computeNewState where we
            // already sync. on AlarmLogic.this
            delayed_check = null;
            return super.cancel();
        }

        /** Invoked by timer after delay. */
        @Override
        synchronized public void run()
        {
            //System.out.println("Delayed alarm check becomes active: " + state);
            delayed_check = null;
            //  Re-evaluate alarm logic with the delayed state,
            //  not allowing any further delays.
            updateState(state, false);
        }
    }
    
    /** @see #getMaintenanceMode() */
    private static volatile boolean maintenance_mode = false;

    /** Is logic enabled, or only following the 'current' PV state
     *  without actually alarming?
     */
    private volatile boolean enabled = true;

    /** Pending delayed alarm state update or <code>null</code> */
    private volatile DelayedAlarmCheck delayed_check = null;
    
    /** Latch the highest received alarm severity/status?
     *  When <code>false</code>, the latched alarm state actually
     *  follows the current value of the PV without requiring
     *  acknowledgment.
     */
    private boolean latching;
    
    /** Annunciate alarms? */
    private boolean annunciating;
    
    /** Require minimum time [seconds] in alarm before indicating alarm */
    private int delay;
    
    /** Alarm when PV != OK more often than this count within delay */
    private AlarmStateHistory alarm_history = null;
    
    /** Current state of the control system channel */
    private AlarmState current_state;

    /** Alarm logic state, with might be latched or delayed from current_state */
    private AlarmState alarm_state;

    /** 'Current' state that was received while disabled.
     *  Is cached in case we get re-enabled, whereupon it is used
     */
    private volatile AlarmState disabled_state = null;
    
    /** Initialize
     *  @param latching Latch the highest received alarm severity?
     *  @param annunciating Annunciate alarms?
     *  @param delay Minimum time in alarm before indicating alarm [seconds]
     *  @param count Alarm when PV != OK more often than this count within delay
     *  @param current_state Current alarm state of PV
     *  @param alarm_state Alarm logic state
     */
    public AlarmLogic(final boolean latching, final boolean annunciating,
            final int delay,
            final int count,
            final AlarmState current_state,
            final AlarmState alarm_state)
    {
        this.latching = latching;
        this.annunciating = annunciating;
        this.delay = delay;
        if (count > 0)
            alarm_history = new AlarmStateHistory(count);
        this.current_state = current_state;
        this.alarm_state = alarm_state;
    }
    
    /** Set maintenance mode.
     *  @param maintenance_mode
     *  @see #getMaintenanceMode()
     */
    @SuppressWarnings("nls")
    public static void setMaintenanceMode(final boolean maintenance_mode)
    {
        AlarmLogic.maintenance_mode = maintenance_mode;
        CentralLogger.getInstance().getLogger(AlarmLogic.class).info("Maintenance Mode: " + maintenance_mode);
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
        if (this.enabled == enable)
            return;
        this.enabled = enable;
        fireEnablementUpdate();
        if (!enabled)
        {   // Disabled
            synchronized (this)
            {   // Remember current PV state in case we're re-enabled
                disabled_state = current_state;
                // Otherwise pretend all is OK, using special message
                current_state = AlarmState.createClearState();
                alarm_state = new AlarmState(SeverityLevel.OK,
                        Messages.AlarmMessageDisabled, "", //$NON-NLS-1$
                        TimestampFactory.now());
            }
            fireStateUpdates();
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
    synchronized public boolean isEnabled()
    {
        return enabled;
    }
    
    /** @param annunciating <code>true</code> to annunciate */
    synchronized public void setAnnunciate(boolean annunciating)
    {
        this.annunciating = annunciating;
    }

    /** @return <code>true</code> if configured to annunciate */
    synchronized public boolean isAnnunciating()
    {
        return annunciating;
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
    synchronized public void setDelay(final int seconds)
    {
        delay = seconds;
    }
    
    /** @return Alarm delay in seconds */
    synchronized public int getDelay()
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

    /** @return <code>true</code> if this is a 'priority' alarm
     *          where INVALID should still be annunciated in maintenance mode,
     *          and the annunciator will not suppress it within a flurry of
     *          alarms that are usually throttled/summarized
     */
    public boolean isPriorityAlarm()
    {
        return false;
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
    @SuppressWarnings("nls")
    public void computeNewState(final AlarmState received_state)
    {
        synchronized (this)
        {
            // When disabled, ignore...
            if (!enabled)
            {
                disabled_state = received_state;
                return;
            }
            // Remember what used to be the 'current' severity
            final SeverityLevel previous_severity = current_state.getSeverity();
            // Update current severity.
            current_state = received_state;
            // If there's no change to the current severity, we're done.
            if (received_state.getSeverity() == previous_severity)
                return;
            
            // Does this 'clear' an acknowledged severity?
            // - or -
            // are we in maintenance mode, and this is a new
            // alarm after an INVALID one that was suppressed?
            if ((current_state.getSeverity() == SeverityLevel.OK  &&
                 !alarm_state.getSeverity().isActive())
                 ||
                (maintenance_mode &&
                 (alarm_state.getSeverity() == SeverityLevel.INVALID_ACK  ||
                  alarm_state.getSeverity() == SeverityLevel.INVALID)  &&
                 current_state.getSeverity() != SeverityLevel.INVALID))
            {
                alarm_state = new AlarmState(SeverityLevel.OK,
                        SeverityLevel.OK.getDisplayName(), "",
                        received_state.getTime());
                // If a timer was started, cancel it
                if (delayed_check != null)
                    delayed_check.cancel();
            }
        }
        updateState(received_state, delay > 0);
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
        SeverityLevel annunc_level = null;
        synchronized (this)
        {
            // Update alarm state. If there is already an update pending,
            // update that delayed state check.
            final AlarmState state_to_update = delayed_check != null
                ? delayed_check.getState()
                : alarm_state;
            final AlarmState new_state = latchAlarmState(state_to_update, received_state);
            // Computed a new alarm state? Else: Only current_severity update
            if (new_state != null)
            {
                // Delay if requested and this is indeed triggered by alarm, not OK
                if (with_delay && received_state.getSeverity() != SeverityLevel.OK)
                {   // Start or update delayed alarm check
                    if (delayed_check == null)
                        delayed_check = new DelayedAlarmCheck(new_state, delay);
                    else
                        delayed_check.update(new_state);
                    // Somewhat in parallel, check for alarm counts
                    if (checkCount(received_state))
                    {   // Exceeded alarm count threshold; reset delayed alarms
                        delayed_check.cancel();
                        // Annunciate if going to higher alarm severity
                        if (annunciating &&
                            new_state.hasHigherUpdatePriority(alarm_state))
                            annunc_level = new_state.getSeverity();
                        alarm_state = new_state;
                    }
                }
                else
                {   // Annunciate if going to higher alarm severity
                    if (annunciating &&
                        new_state.hasHigherUpdatePriority(alarm_state))
                        annunc_level = new_state.getSeverity();
                    alarm_state = new_state;
                }
            }
        }
        // In maint. mode, INVALID is automatically ack'ed and not annunciated,
        // except for 'priority' alarms
        if (maintenance_mode && 
            !isPriorityAlarm() &&
            alarm_state.getSeverity() == SeverityLevel.INVALID)
        {
            alarm_state = alarm_state.createAcknowledged(alarm_state);
            annunc_level = null;
        }
        fireStateUpdates();
        if (annunc_level != null)
            fireAnnunciation(annunc_level);
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

    /** Acknowledge current alarm severity
     *  @param acknowledge Acknowledge or un-acknowledge?
     */
    public void acknowledge(boolean acknowledge)
    {
        synchronized (this)
        {
            if (acknowledge)
            {   // Does this actually 'clear' an acknowledged severity?
                if (current_state.getSeverity() == SeverityLevel.OK)
                    alarm_state = AlarmState.createClearState();
                else
                    alarm_state = alarm_state.createAcknowledged(current_state);
            }
            else
                alarm_state = alarm_state.createUnacknowledged();
        }
        fireStateUpdates();
    }

    /** Invoked when enablement changes.
     *  @see #isEnabled()
     */
    abstract protected void fireEnablementUpdate();

    /** Invoked on change in alarm state, current or latched,
     *  to allow for notification of clients.
     */
    abstract protected void fireStateUpdates();
    
    /** Invoked when annunciation is required.
     *  @param level Level to annunciate
     */
    abstract protected void fireAnnunciation(SeverityLevel level);
    
    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Current: " + current_state + " / Alarm: " + alarm_state;
    }
}
