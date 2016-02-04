/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.diirt.vtype.AlarmSeverity;

/** BEAST alarm info for PV Widgets.
 *
 * @author Boris Versic
 */
public final class BeastAlarmInfo {
    private String alarmPVChannelName;
    private boolean isBeastChannelConnected;
    public BeastAlarmSeverityLevel latchedSeverity;
    public BeastAlarmSeverityLevel currentSeverity;

    /**
     * The blinking "state" for the Beast Alarm alert: 0 = default color, 1 = severity color.
     */
    public int beastAlertBlinkState = 0;

    public BeastAlarmInfo() {
        alarmPVChannelName = "";
        isBeastChannelConnected = false;
        latchedSeverity = BeastAlarmSeverityLevel.OK;
        currentSeverity = BeastAlarmSeverityLevel.OK;
    }

    public void setBeastChannelName(String channelName) {
        alarmPVChannelName = channelName;
    }

    public String getBeastChannelName() {
        return alarmPVChannelName;
    }

    public String getBeastChannelNameNoScheme() {
        if (alarmPVChannelName.length() > 8)
            return alarmPVChannelName.substring(8);
        return "";
    }

    /** @return <code>true</code> if (latched) severity indicates an acknowledged state,
     *          <code>false</code> for unacknowledged alarm or OK
     */
    public boolean isAcknowledged() {
        return !latchedSeverity.isActive() && latchedSeverity != BeastAlarmSeverityLevel.OK;
    }

    /** @return <code>true</code> if (latched) severity indicates an active alarm,
     *          <code>false</code> for acknowledged or OK state
     */
    public boolean isLatchedAlarmActive() {
        return latchedSeverity.isActive();
    }

    /** @return <code>true</code> if (current) severity indicates an active alarm,
     *          <code>false</code> for acknowledged or OK state
     */
    public boolean isCurrentAlarmActive() {
        return currentSeverity.isActive();
    }

    public void reset() {
        latchedSeverity = BeastAlarmSeverityLevel.OK;
        currentSeverity = BeastAlarmSeverityLevel.OK;
    }

    public boolean isBeastChannelConnected() {
        return isBeastChannelConnected;
    }

    public void setBeastChannelConnected(boolean connected) {
        isBeastChannelConnected = connected;
    }

    public boolean isLatchedAlarmOK() {
        return latchedSeverity == BeastAlarmSeverityLevel.OK;
    }

    public AlarmSeverity getLatchedSeverity() {
        return latchedSeverity.getAlarmSeverity();
    }
}