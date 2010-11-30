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

import org.apache.log4j.Logger;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.osgi.util.NLS;

/** A PV with alarm state
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmPV extends AlarmHierarchy implements AlarmLogicListener, PVListener, FilterListener
{
    /** Timer used to check for connections at some delay after 'start */
    final private static Timer connection_timer =
        new Timer("Connection Check", true);

    final private Logger log;

    final private AlarmLogic logic;

    /** Alarm server that handles this PV */
    final private AlarmServer server;

    /** Description of alarm, will be used to annunciation */
    private volatile String description;

    /** Control system PV */
    final private PV pv;

    /** Started when pv is created to check if it ever connects */
    private TimerTask connection_timeout_task = null;

    /** Filter that might be used to compute 'enabled' state;
     *  can be <code>null</code>
     */
    private volatile Filter filter;

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
    		final AlarmHierarchy parent,
    		final int id, final String name,
            final String description,
            final boolean enabled,
            final boolean latching,
            final boolean annunciating,
            final int min_alarm_delay,
            final int count,
            final String filter,
            final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel severity,
            final String message,
            final String value,
            final ITimestamp timestamp) throws Exception
    {
    	super(parent, name, id);
    	// PV has no child entries
    	setChildren(new AlarmHierarchy[0]);

    	// TODO Configure global_delay
    	final int global_delay = 0;

    	logic = new AlarmLogic(this, latching, annunciating, min_alarm_delay, count,
              new AlarmState(current_severity, current_message, "", timestamp),
              new AlarmState(severity, message, value, timestamp), global_delay);
        log = CentralLogger.getInstance().getLogger(this);
        this.server = server;
        setDescription(description);
        if (server == null)
        {	// Unit test, don't create a PV
        	pv = null;
        }
        else
        {
	        pv = PVFactory.createPV(name);
	        setEnablement(enabled, filter);
        }
        alarm_severity = severity;
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
        final String basic_description;
        if (description.startsWith(Messages.BasicAnnunciationPrefix))
            basic_description = description.substring(Messages.BasicAnnunciationPrefix.length()).trim();
        else
            basic_description = description.trim();
        logic.setPriority(basic_description.startsWith("!"));
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
        if (pv.isRunning())
            throw new Exception("Cannot change enablement while running");
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
                if (pv.isRunning() && !pv.isConnected())
                    pvConnectionTimeout();
            }
        };
        connection_timer.schedule(connection_timeout_task, delay);
        pv.addListener(this);
        pv.start();
        if (filter != null)
            filter.start();
    }

    /** Disconnect from control system */
    public void stop()
    {
        if (connection_timeout_task != null)
        {
            connection_timeout_task.cancel();
            connection_timeout_task = null;
        }
    	if (filter != null)
    		filter.stop();
        pv.removeListener(this);
        pv.stop();
    }

    /** @see FilterListener */
    public void filterChanged(final double value)
    {
    	final boolean new_enable_state = value > 0.0;
        if (log.isDebugEnabled())
            log.debug(getName() + " filter " +
                      (new_enable_state ? "enables" : "disables"));
        logic.setEnabled(new_enable_state);
	}

    /** Invoked by <code>connection_timer</code> when PV fails to connect
     *  after <code>start()</code>
     */
	private void pvConnectionTimeout()
    {

	    final AlarmState received = new AlarmState(SeverityLevel.INVALID,
               Messages.AlarmMessageNotConnected, "", TimestampFactory.now());
	    logic.computeNewState(received);
    }

    /** @see PVListener */
    public void pvDisconnected(final PV pv)
    {
        // Also ignore the disconnect event that can result from stop()
    	if (!pv.isRunning())
    		return;
        final AlarmState received = new AlarmState(SeverityLevel.INVALID,
                Messages.AlarmMessageDisconnected, "", TimestampFactory.now());
        logic.computeNewState(received);
    }

    /** @see PVListener */
    public void pvValueUpdate(final PV pv)
    {
    	// Inspect alarm state of received value
        final IValue value = pv.getValue();
        final SeverityLevel new_severity = decodeSeverity(value);
        final String new_message = value.getStatus();
        final AlarmState received = new AlarmState(new_severity, new_message,
                value.format(), value.getTime());
        logic.computeNewState(received);
    }

    /** Decode a value's severity
     *  @param value Value to decode
     *  @return SeverityLevel
     */
    private SeverityLevel decodeSeverity(final IValue value)
    {
        final ISeverity sev = value.getSeverity();
        if (sev.isInvalid())
            return SeverityLevel.INVALID;
        if (sev.isMajor())
            return SeverityLevel.MAJOR;
        if (sev.isMinor())
            return SeverityLevel.MINOR;
        return SeverityLevel.OK;
    }

	/** AlarmLogicListener: {@inheritDoc} */
    public void alarmEnablementChanged(final boolean is_enabled)
    {
        server.sendEnablementUpdate(this, is_enabled);
    }

	/** AlarmLogicListener: {@inheritDoc} */
    public void alarmStateChanged(final AlarmState current, final AlarmState alarm)
    {
        if (log.isDebugEnabled())
            log.debug(getName() + " changes to " + super.toString());
        if (server != null)
            server.sendStateUpdate(this,
                    current.getSeverity(), current.getMessage(),
                    alarm.getSeverity(), alarm.getMessage(),
                    alarm.getValue(), alarm.getTime());
        // Update alarm tree 'upwards' from this leaf
        alarm_severity = alarm.getSeverity();
        parent.maximizeSeverity();
    }

	/** AlarmLogicListener: {@inheritDoc} */
    public void annunciateAlarm(final SeverityLevel level)
    {
        final String message;
        // For annunciation texts like "* Some Message" where
        // "*" is the BasicAnnunciationPrefix, remove the prefix
        // and use the basic format.
        // Otherwise use the severity level and the text with the
        // normal AnnunciationFmt
        if (description.startsWith(Messages.BasicAnnunciationPrefix))
            message = NLS.bind(Messages.BasicAnnunciationFmt,
                               description.substring(Messages.BasicAnnunciationPrefix.length()));
        else
            message = NLS.bind(Messages.AnnunciationFmt,
                               level.getDisplayName(), description);
        if (server != null)
            server.sendAnnunciation(level, message);
    }

	/** AlarmLogicListener: {@inheritDoc} */
    public void globalStateChanged(final AlarmState alarm)
    {
	    // TODO Persist global alarm state change, send to JMS
    }

	/** @return String representation for debugging (server 'dump') */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(getPathName() + " [" + description + "] - ");
        if (pv != null)
        {
	        if (pv.isConnected())
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
            buf.append(logic.getDelay() + " sec delay - ");
        if (filter != null)
            buf.append(filter.toString() + " - ");
        buf.append(logic.toString());
        return buf.toString();
    }
}
