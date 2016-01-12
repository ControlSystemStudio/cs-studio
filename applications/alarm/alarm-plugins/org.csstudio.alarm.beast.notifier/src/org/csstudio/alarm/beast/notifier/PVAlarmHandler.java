/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.notifier.history.AlarmNotifierHistory;
import org.csstudio.alarm.beast.notifier.history.PVHistoryEntry;
import org.eclipse.osgi.util.NLS;

/**
 * Handler for alarm updates. Receive alarm updates and store information. Used
 * {@link AbstractNotificationAction} to validate if an automated action can be
 * executed or not regarding to last alarm updates.
 *
 * @author Fred Arnaud (Sopra Group)
 * @author Kay Kasemir - notify only on escalating alarms, idea by Xinyu Wu
 */
public class PVAlarmHandler {
    final public static boolean notify_escalating_alarms_only = org.csstudio.alarm.beast.notifier.Preferences.getNotifyEscalatingAlarmsOnly();

    private List<PVSnapshot> snapshots;
    private EActionStatus status = EActionStatus.PENDING;
    private String reason = Messages.Empty;
    private boolean acknowledged = false;

    public PVAlarmHandler() {
        this.snapshots = new LinkedList<PVSnapshot>();
    }

    public void update(PVSnapshot snapshot) {
        if (getCurrent() != null) {
            SeverityLevel previous_severity = getCurrent().getSeverity();
            if (unACK(previous_severity, snapshot.getSeverity()))
                this.acknowledged = false;
        }
        if (!acknowledged && snapshot.isAcknowledge())
            this.acknowledged = true;
        snapshots.add(snapshot);
        validate();
    }

    public PVSnapshot getCurrent() {
        if (snapshots.isEmpty())
            return null;
        return snapshots.get(snapshots.size() - 1);
    }

    private void validate() {
        final PVSnapshot current_snapshot = getCurrent();
        final AlarmNotifierHistory history = AlarmNotifierHistory.getInstance();
        final PVHistoryEntry pv_history = history.getPV(current_snapshot.getPath());

        if (notify_escalating_alarms_only)
        {   // Cancel alarms that are not escalating the severity
            final SeverityLevel current = current_snapshot.getCurrentSeverity(),
                                latched = current_snapshot.getSeverity();
            if (! latched.isActive())
            {
                this.status = EActionStatus.CANCELED;
                this.reason = Messages.Reason_Acknowledged;
                history.clear(current_snapshot);
                return;
            }
            else if (! current.isActive()) // For now really same as current == OK
            {
                 this.status = EActionStatus.CANCELED;
                 this.reason = NLS.bind(Messages.Reason_RecoveredFmt, latched.name(), current.name());
                 history.clear(current_snapshot);
                 return;
            }
        }

        if (snapshots.size() == 1) {
            if (pv_history == null) {
                if (current_snapshot.getSeverity().equals(SeverityLevel.OK)) {
                    // Configuration update results in an alarm update with no changes => CANCEL
                    this.status = EActionStatus.CANCELED;
                    this.reason = Messages.Reason_NoAlarmRaised;
                    return;
                }
                // If no history entry exists, it is the first alarm update => WAIT
                this.status = EActionStatus.PENDING;
                this.reason = Messages.Empty;
            } else {
                if (current_snapshot.getSeverity().equals(pv_history.getSeverity())
                        && current_snapshot.getCurrentSeverity().equals(pv_history.getCurrentSeverity())) {
                    // Configuration update results in an alarm update with no changes => CANCEL
                    this.status = EActionStatus.CANCELED;
                    this.reason = Messages.Reason_NoAlarmRaised;
                    return;
                }
                // If an history entry exists, PV was already under alarm
                if (pv_history.isAcknowledged()) {
                    if (unACK(pv_history.getSeverity(), current_snapshot.getSeverity())) {
                        // Un-acknowledged alarm state change after the delay of previous ACK => NO DELAY
                        this.status = EActionStatus.NO_DELAY;
                        this.reason = Messages.Reason_NoDelay;
                    } else {
                        // Acknowledged alarm state change with lower/higher or OK priority => CANCEL
                        this.status = EActionStatus.CANCELED;
                        this.reason = Messages.Reason_Acknowledged;
                    }
                } else {
                    if (pv_history.hasRecovredWithinDelay()
                            && current_snapshot.isAcknowledge()) {
                        // Acknowledge after alarm state change and recover within delay => CANCEL
                        this.status = EActionStatus.CANCELED;
                        this.reason = Messages.Reason_Recovered;
                    } else {
                        // Alarm acknowledged after delay of last action => NO DELAY
                        // Un-acknowledged alarm state change => NO DELAY
                        this.status = EActionStatus.NO_DELAY;
                        this.reason = Messages.Reason_NoDelay;
                    }
                }
            }
        } else {
            if (!current_snapshot.isUnderAlarm()) {
                // Alarm raised & recovered within the delay => CANCEL
                this.status = EActionStatus.CANCELED;
                this.reason = Messages.Reason_Recovered;
            } else {
                if (acknowledged) {
                    // Alarm raised & acknowledged within the delay => CANCEL NO DELAY
                    this.status = EActionStatus.CANCELED_NO_DELAY;
                    this.reason = Messages.Reason_Acknowledged;
                } else {
                    PVSnapshot previous_snapshot = snapshots.get(snapshots.size() - 2);
                    if (pv_history != null
                            && pv_history.isAcknowledged()
                            && !unACK(previous_snapshot.getSeverity(), current_snapshot.getSeverity())) {
                        // Acknowledged alarm state change with lower/higher or OK priority => CANCEL NO DELAY
                        this.status = EActionStatus.CANCELED_NO_DELAY;
                        this.reason = Messages.Reason_Acknowledged;
                    } else {
                        // Default: we wait for the delay
                        this.status = EActionStatus.PENDING;
                        this.reason = Messages.Empty;
                    }
                }
            }
        }
        // TODO: if a update occurs before the previous one has finished
        // processing, history is null...
        if (pv_history != null) {
            if (snapshots.size() == 1) pv_history.setRecovredWithinDelay(false);
            else pv_history.setRecovredWithinDelay(!current_snapshot.isUnderAlarm());
        }
    }

    /** @param previous Previous severity
     *  @param current Current severity
     *  @return Is current the un-acked version of the previous {@link SeverityLevel}?
     */
    private boolean unACK(final SeverityLevel previous, final SeverityLevel current)
    {
        return current.isActive()  == true   &&
               previous.isActive() == false  &&
               previous.name().startsWith(current.name());
    }

    public EActionStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

}
