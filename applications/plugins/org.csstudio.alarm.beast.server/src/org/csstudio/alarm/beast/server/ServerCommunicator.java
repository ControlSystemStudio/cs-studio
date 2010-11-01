/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.text.SimpleDateFormat;

import javax.jms.MapMessage;

import org.csstudio.alarm.beast.JMSAlarmCommunicator;
import org.csstudio.alarm.beast.JMSAlarmMessage;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimeoutTimer;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;

/** Communicates alarm system updates between server and clients.
 *  @author Kay Kasemir
 */
public class ServerCommunicator extends JMSAlarmCommunicator
{
    /** Format of time stamps */
    final private SimpleDateFormat date_format =
        new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);
    
    /** Server for which we communicate */
    final private AlarmServer server;
    
    /** Work queue in main application */
    final private WorkQueue work_queue;

    /** Timer for sending idle messages */
    final private TimeoutTimer idle_timer;

    /** Initialize communicator that writes to the 'server' topic
     *  and listens to 'client' topic messages
     *  @param server Alarm server
     *  @param work_queue 
     */
    public ServerCommunicator(final AlarmServer server, final WorkQueue work_queue) throws Exception
    {
        super(server.getRootName(),
              Preferences.getJMS_AlarmServerTopic(server.getRootName()),
              Preferences.getJMS_AlarmClientTopic(server.getRootName()), false);
        this.server = server;
        this.work_queue = work_queue;
        idle_timer = new TimeoutTimer(Preferences.getJMS_IdleTimeout()*1000)
        {
            @Override
            protected void timeout()
            {
                sendIdleMessage();
            }
        };
        // Send initial idle message right away; more via timer
        sendIdleMessage();
    }    
    
    /** Start the communicator */
    @Override
    public void start()
    {
    	super.start();
    	idle_timer.start();
    }

    /** {@inheritDoc} */
    @Override
    synchronized public void close()
    {
        idle_timer.cancel();
        try
        {
            idle_timer.join();
        }
        catch (InterruptedException ex)
        {
            CentralLogger.getInstance().getLogger(this)
                .warn("Idle Timer join failed", ex); //$NON-NLS-1$
        }
        super.close();
    }

    /** Create message initialized with basic alarm & application info
     *  @param text TEXT property
     *  @return MapMessage
     *  @throws Exception on error.
     */
    private MapMessage createMapMessage(final String text) throws Exception
    {
        return createBasicMapMessage(Application.APPLICATION_NAME,
                                    JMSAlarmMessage.TYPE_ALARM, text);
    }
    
    /** Send idle message, which includes the operating state.
     *  This is usually invoked by the idle_timer,
     *  but can be invoked on purpose to update clients in mode changes ASAP.
     */
    protected void sendIdleMessage()
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        final MapMessage map = createMapMessage(
                               AlarmLogic.getMaintenanceMode()
                               ? JMSAlarmMessage.TEXT_IDLE_MAINTENANCE
                               : JMSAlarmMessage.TEXT_IDLE);
                        producer.send(map);
                    }
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error("Exception", ex); //$NON-NLS-1$
                }
            }
        });
        idle_timer.reset();
    }

    /** Notify clients that they should re-load the configuration:
     *  RDB might be out of sync because there were connection problems.
     */
    public void sendReloadMessage()
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        final MapMessage map = createMapMessage(JMSAlarmMessage.TEXT_CONFIG);
                        producer.send(map);
                        // Inform idle timer
                        idle_timer.reset();
                    }
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error("Exception", ex); //$NON-NLS-1$
                }
            }
        });
    }

    /** Notify clients of new alarm state
     *  @param pv PV that changes alarm state
     *  @param current_severity Current severity of the PV
     *  @param current_message Current message of the PV
     *  @param alarm_severity Alarm severity
     *  @param alarm_message Alarm message
     *  @param value Value that triggered update
     *  @param timestamp Time stamp for alarm severity/status
     */
    protected void sendStateUpdate(final AlarmPV pv,
            final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel alarm_severity, final String alarm_message,
            final String value,
            final ITimestamp timestamp)
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        final MapMessage map = createMapMessage(
                                AlarmLogic.getMaintenanceMode()
                                ? JMSAlarmMessage.TEXT_STATE_MAINTENANCE
                                : JMSAlarmMessage.TEXT_STATE);
                        map.setString(JMSLogMessage.NAME, pv.getName());
                        map.setString(JMSLogMessage.SEVERITY, alarm_severity.name());
                        map.setString(JMSAlarmMessage.STATUS,  alarm_message);
                        if (value != null)
                            map.setString(JMSAlarmMessage.VALUE, value);
                        map.setString(JMSLogMessage.EVENTTIME, date_format.format(timestamp.toCalendar().getTime()));
                        map.setString(JMSAlarmMessage.CURRENT_SEVERITY, current_severity.name());
                        map.setString(JMSAlarmMessage.CURRENT_STATUS, current_message);
                        producer.send(map);
                        // Inform idle timer
                        idle_timer.reset();
                    }
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error("Exception", ex); //$NON-NLS-1$
                }
            }
        });
    }

    /** Notify clients of enablement state
     *  @param pv PV that is now enabled/disabled
     *  @param enabled Enabled?
     */
    protected void sendEnablementUpdate(final AlarmPV pv, final boolean enabled)
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                final String text = enabled ? JMSAlarmMessage.TEXT_ENABLE
                                            : JMSAlarmMessage.TEXT_DISABLE;
                try
                {
                    synchronized (this)
                    {
                        final MapMessage map = createMapMessage(text);
                        map.setString(JMSLogMessage.NAME, pv.getName());
                        producer.send(map);
                        // Inform idle timer
                        idle_timer.reset();
                    }
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error("Exception", ex); //$NON-NLS-1$
                }
            }
        });
	}

	/** Handle messages received from alarm clients.
     *  <p>
     *  This is invoked from a JMS thread.
     *  There have been deadlocks when trying to reconfigure the
     *  server from within here, when that resulted in PVs being
     *  created or stopped, while at the same time values might
     *  arrive from other PVs.
     *  The ultimate problem is somewhere inside JNI Channel Access,
     *  but to be on the save side we run model config updates
     *  in the main thread.
     */
    @SuppressWarnings("nls")
    @Override
    protected void handleMapMessage(final MapMessage message)
    {
        try
        {
            final String name = message.getString(JMSLogMessage.NAME);
            final String text = message.getString(JMSLogMessage.TEXT);
            // Acknowledge request?
            if (JMSAlarmMessage.TEXT_ACKNOWLEDGE.equals(text))
                server.acknowledge(name, true);
            else if (JMSAlarmMessage.TEXT_UNACKNOWLEDGE.equals(text))
                server.acknowledge(name, false);
            else if (JMSAlarmMessage.TEXT_CONFIG.equals(text))
                work_queue.add(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            server.updateConfig(name);
                        }
                        catch (Exception ex)
                        {
                            CentralLogger.getInstance()
                                .getLogger(ServerCommunicator.this).error(ex);
                        }
                    }
                });
            else if (JMSAlarmMessage.TEXT_MODE.equals(text))
            {
                final String mode = message.getString(JMSAlarmMessage.VALUE);
                if (JMSAlarmMessage.VALUE_MODE_NORMAL.equals(mode))
                    server.setMaintenanceMode(false);
                else if (JMSAlarmMessage.VALUE_MODE_MAINTENANCE.equals(mode))
                    server.setMaintenanceMode(true);
                else
                    CentralLogger.getInstance().getLogger(this).warn("Unknown MODE request '" + mode + "'");
            }
            else if (JMSAlarmMessage.TEXT_DEBUG.equals(text))
                server.dump();
        }
        catch (Exception ex)
        {
            CentralLogger.getInstance().getLogger(this).error("Exception", ex);
        }
    }
}
