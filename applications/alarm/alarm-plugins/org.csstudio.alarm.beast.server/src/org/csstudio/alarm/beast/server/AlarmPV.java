/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;


import java.io.PrintStream;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.csstudio.alarm.beast.AnnunciationFormatter;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVPool;
import org.diirt.vtype.VType;

/** A PV with alarm state
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmPV extends TreeItem implements AlarmLogicListener, FilterListener, PVListener
{
    private static final long serialVersionUID = -1467537752626320944L;

    /** Timer used to check for connections at some delay after 'start */
    final private static Timer connection_timer =
        new Timer("Connection Check", true);

    final private transient AlarmLogic logic;

    /** Alarm server that handles this PV */
    final private transient AlarmServer server;

    /** Description of alarm, will be used to annunciation */
    private volatile String description;

    /** Control system PV */
    private volatile transient PV pv = null;

    /** Track connection state */
    private volatile boolean is_connected = false;

    /** Started when pv is created to check if it ever connects */
    private transient TimerTask connection_timeout_task = null;

    /** Filter that might be used to compute 'enabled' state;
     *  can be <code>null</code>
     */
    private volatile transient Filter filter;

    /** Initialize alarm PV
     *  @param server Alarm server that handles this PV. Within JUnit tests, this may be <code>null</code>.
     *  @param id RDB ID
     *  @param name Name of control system PV
     *  @param description Description of alarm, will be used to annunciation
     *  @param enabled Enable the alarm logic?
     *  @param latching Latch the highest received alarm severity?
     *  @param annunciating Annunciate alarms?
     *  @param min_alarm_delay Minimum time in alarm before declaring an alarm
     *  @param count Alarm when PV != OK more often than this count within delay
     *  @param global_delay 'Global' alarm delay [seconds] or 0
     *  @param filter Filter expression for enablement or <code>null</code>
     *  @param current_severity Current alarm severity
     *  @param current_message Current system message
     *  @param severity Alarm system severity
     *  @param message Alarm system message
     *  @param value Value
     *  @param timestamp
     *  @throws Exception on error
     */
    public AlarmPV(final AlarmServer server,
            final TreeItem parent,
            final int id, final String name,
            final String description,
            final boolean enabled,
            final boolean latching,
            final boolean annunciating,
            final int min_alarm_delay,
            final int count,
            final int global_delay,
            final String filter,
            final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel severity,
            final String message,
            final String value,
            final Instant timestamp) throws Exception
    {
        super(parent, name, id);

        logic = new AlarmLogic(this, latching, annunciating, min_alarm_delay, count,
              new AlarmState(current_severity, current_message, "", timestamp),
              new AlarmState(severity, message, value, timestamp), global_delay);
        this.server = server;
        setDescription(description);
        setEnablement(enabled, filter);
    }

    /** @return AlarmLogic used by this PV */
    AlarmLogic getAlarmLogic()
    {
        return logic;
    }

    /** @return Alarm description */
    public String getDescription()
    {
        return description;
    }

    /** @param description Alarm description */
    public void setDescription(final String description)
    {
        this.description = description;
        // Determine 'priority' based on description
        logic.setPriority(AnnunciationFormatter.hasPriority(description));
    }

    /** Set enablement.
     *  <p>
     *  Must not be called on a running PV.
     *  @param enabled Enable state that's used in absence of filter
     *  @param filter New filter expression, may be <code>null</code>
     *  @throws Exception on error
     */
    void setEnablement(final boolean enabled, final String filter) throws Exception
    {
        synchronized (logic)
        {
            if (filter == null  ||  filter.length() <= 0)
                this.filter = null;
            else
                this.filter = new Filter(filter, this);
            logic.setEnabled(enabled);
        }
    }

    /** Connect to control system */
    public void start() throws Exception
    {
        // Seconds to millisecs
        final long delay = Preferences.getConnectionGracePeriod() * 1000;
        connection_timeout_task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (! is_connected)
                    pvConnectionTimeout();
            }
        };
        connection_timer.schedule(connection_timeout_task, delay);

        pv = PVPool.getPV(getName());
        pv.addListener(this);
        if (filter != null)
            filter.start();
    }

    /** Disconnect from control system */
    public void stop()
    {
        //the alarm pv has been stopped already
        if (pv == null)
            return;
        if (connection_timeout_task != null)
        {
            connection_timeout_task.cancel();
            connection_timeout_task = null;
        }
        if (filter != null)
            filter.stop();
        pv.removeListener(this);
        PVPool.releasePV(pv);
        pv = null;
        is_connected = false;
    }

    /** @return <code>true</code> if PV is connected */
    public boolean isConnected()
    {
        return is_connected;
    }

    /** @see FilterListener */
    @Override
    public void filterChanged(final double value)
    {
        final boolean new_enable_state = value > 0.0;
        Activator.getLogger().log(Level.FINE, "{0} filter changed to {1}",
                new Object[] { getName(), new_enable_state });
        logic.setEnabled(new_enable_state);
    }

    /** Invoked by <code>connection_timer</code> when PV fails to connect
     *  after <code>start()</code>
     */
    private void pvConnectionTimeout()
    {
        final AlarmState received = new AlarmState(SeverityLevel.UNDEFINED,
            Messages.AlarmMessageNotConnected, "", Instant.now());
        logic.computeNewState(received);
    }

    /** @see PVListener */
    @Override
    public void permissionsChanged(PV pv, boolean readonly)
    {
        // NOP
    }

    /** @see PVListener */
    @Override
    public void disconnected(final PV pv)
    {
        final AlarmState received = new AlarmState(SeverityLevel.UNDEFINED,
                Messages.AlarmMessageDisconnected, "", Instant.now());
        logic.computeNewState(received);
    }

    /** @see PVListener */
    @Override
    public void valueChanged(final PV pv, final VType value)
    {   // Inspect alarm state of received value
        is_connected = true;
        final SeverityLevel new_severity = VTypeHelper.decodeSeverity(value);
        final String new_message = VTypeHelper.getStatusMessage(value);
        final AlarmState received = new AlarmState(new_severity, new_message,
                VTypeHelper.toString(value),
                VTypeHelper.getTimestamp(value));
        logic.computeNewState(received);
    }

    /** AlarmLogicListener: {@inheritDoc} */
    @Override
    public void alarmEnablementChanged(final boolean is_enabled)
    {
        server.sendEnablementUpdate(this, is_enabled);
    }

    /** AlarmLogicListener: {@inheritDoc} */
    @Override
    public void alarmStateChanged(final AlarmState current, final AlarmState alarm)
    {
        Activator.getLogger().log(Level.FINE, "{0} changes to {1}, {2}",
                new Object[] { getName(), current, alarm });
        if (server != null)
            server.sendStateUpdate(this,
                    current.getSeverity(), current.getMessage(),
                    alarm.getSeverity(), alarm.getMessage(),
                    alarm.getValue(), alarm.getTime());
    }

    /** AlarmLogicListener: {@inheritDoc} */
    @Override
    public void annunciateAlarm(final SeverityLevel level)
    {
        final String value = getAlarmLogic().getAlarmState().getValue();
        final String message = AnnunciationFormatter.format(description, level.getDisplayName(), value);

        if (server != null)
            server.sendAnnunciation(level, message);
    }

    /** AlarmLogicListener: {@inheritDoc} */
    @Override
    public void globalStateChanged(final AlarmState alarm)
    {
        Activator.getLogger().log(Level.FINE, "{0} has global state {1}",
            new Object[] { getName(), alarm });
        if (server != null)
            server.sendGlobalUpdate(this,
                    alarm.getSeverity(), alarm.getMessage(),
                    alarm.getValue(), alarm.getTime());
    }


    /** {@inheritDoc} */
    @Override
    protected void dump_item(final PrintStream out, final String indent)
    {
        out.println(indent + "* " + toString());
    }

    /** @return String representation for debugging (server 'dump') */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("'").append(getPathName()).append("' (ID ").append(getID()).append(")");
        buf.append(" [").append(description).append("] - ");
        if (pv != null)
        {
            if (is_connected)
                buf.append("connected - ");
            else
                buf.append("disconnected - ");
        }
        if (! logic.isEnabled())
            buf.append("disabled - ");
        if (logic.isAnnunciating())
            buf.append("annunciating - ");
        if  (logic.isLatching())
            buf.append("latching - ");
        if (logic.getDelay() > 0)
            buf.append(logic.getDelay()).append(" sec delay - ");
        if (filter != null)
            buf.append(filter.toString()).append(" - ");
        buf.append(logic.toString());
        return buf.toString();
    }
}
