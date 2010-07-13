/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.clientmodel;

import java.util.LinkedList;

import javax.jms.MapMessage;

import org.csstudio.alarm.beast.AlarmTreePV;
import org.csstudio.alarm.beast.JMSAlarmCommunicator;
import org.csstudio.alarm.beast.JMSAlarmMessage;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimeoutTimer;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.logging.JMSLogMessage;

/** Receives alarm updates, sends acknowledgments.
 *  <p>
 *  The communicator is started early on in "Queue" mode,
 *  where it queues received events until the application
 *  has fully initialized.
 *  <p>
 *  Then it is switched to "Dispatch" mode, first sending the queued
 *  events, and from then on directly dispatching received events.
 *  
 *  @author Kay Kasemir
 */
class AlarmClientCommunicator extends JMSAlarmCommunicator
{
    /** Application name used when sending JMS messages */
    final private static String APPLICATION = "CSS"; //$NON-NLS-1$

    /** The model */
    final private AlarmClientModel model;

    /** Action to update a PV's state */
    private class UpdateAction implements Runnable
    {
        final private SeverityLevel current_severity, severity;
        final private String name, current_message, message, value;
        final private ITimestamp timestamp;

        public UpdateAction(final String name,
                final SeverityLevel current_severity,
                final String current_message,
                final SeverityLevel severity, final String message,
                final String value,
                final ITimestamp timestamp)
        {
            this.name = name;
            this.current_severity = current_severity;
            this.current_message = current_message;
            this.severity = severity;
            this.message = message;
            this.value = value;
            this.timestamp = timestamp;
        }
        
        public void run()
        {
            model.updatePV(name, current_severity, current_message,
                           severity, message, value, timestamp);
        }

        @SuppressWarnings("nls")
        @Override
        public String toString()
        {
            return "Update " + name + " to " +
               current_severity.getDisplayName() + "/" + current_message + "," +
               severity.getDisplayName() + "/" + message;
        }
    }
    
    /** Action to enable/disable a PV */
    private class EnableAction implements Runnable
    {
        final private String name;
        final private boolean enable;

        public EnableAction(final String name, final boolean enable)
        {
            this.name = name;
            this.enable = enable;
        }
        
        public void run()
        {
            model.updateEnablement(name, enable);
        }

        @SuppressWarnings("nls")
        @Override
        public String toString()
        {
            return "Enable " + name + ": " + enable;
        }
    }

    
    /** Update mode
     *  SYNC on queue
     */
    private boolean use_queue = true;
    
    /** Event queue (filled in Queue mode)
     *  SYNC on queue
     */
    private LinkedList<Runnable> queue = new LinkedList<Runnable>();

    /** Thread that checks for server timeouts.
     *  Using twice the expected idle message rate
     */
    final private TimeoutTimer timeout_timer =
            new TimeoutTimer(Preferences.getJMS_IdleTimeout() * 2000)
    {
        @Override
        protected void timeout()
        {
            model.fireServerTimeout();
        }
    };

    /** Initialize communicator that writes to the 'client' topic
     *  and listens to 'server' topic
     *  @param allow_write Allow write access?
     *  @param model Model for which to communicate
     *  @throws Exception on error
     */
    public AlarmClientCommunicator(final boolean allow_write,
            final AlarmClientModel model) throws Exception
    {
        super("-unknown-", //$NON-NLS-1$
              allow_write ? Preferences.getJMS_AlarmClientTopic() : null,
                            Preferences.getJMS_AlarmServerTopic(),
              true);
        this.model = model;
    }
    
    /** Dispatch queued events,
     *  or directly dispatch received events?
     *  <p>
     *  While reading model information, queue mode should be enabled.
     *  Then, after model information from RDB has been obtained,
     *  disable queue mode which also dispatched all the queued up events.
     *  @param use_queue <code>true</code> to queue
     */
    public void setQueueMode(final boolean use_queue)
    {
        synchronized (queue)
        {
            this.use_queue = use_queue;
            if (use_queue)
                return;
            // Queuing turned off -> Dispatched what has accumulated until now
            Runnable event = queue.poll();
            while (event != null)
            {
                // System.out.println("Running queued action " + event.toString());
                event.run();
                event = queue.poll();
            }
        }
    }
    
    /** Create message initialized with basic alarm & application info
     *  @param text TEXT property
     *  @return MapMessage
     *  @throws Exception on error.
     */
    private MapMessage createMapMessage(final String text) throws Exception
    {
        return createBasicMapMessage(APPLICATION, JMSAlarmMessage.TYPE_ALARM,
                text);
    }

    /** Send request to enable/disable maintenance mode to alarm server
     *  @param maintenance <code>true</code> to enable
     */
    public void requestMaintenanceMode(final boolean maintenance)
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    final MapMessage map =
                        createMapMessage(JMSAlarmMessage.TEXT_MODE);
                    map.setString(JMSAlarmMessage.VALUE,
                                  maintenance
                                  ? JMSAlarmMessage.VALUE_MODE_MAINTENANCE
                                  : JMSAlarmMessage.VALUE_MODE_NORMAL);
                    producer.send(map);
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error(ex);
                }
            }
        });
    }

    /** Ask alarm server to acknowledge alarm.
     *  @param pv PV to acknowledge
     *  @param acknowledge Acknowledge, or un-acknowledge?
     */
    public void requestAcknowledgement(final AlarmTreePV pv, final boolean acknowledge)
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    final MapMessage map = createMapMessage(acknowledge
                            ? JMSAlarmMessage.TEXT_ACKNOWLEDGE
                            : JMSAlarmMessage.TEXT_UNACKNOWLEDGE);
                    map.setString(JMSLogMessage.NAME, pv.getName());
                    producer.send(map);
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error(ex);
                }
            }
        });
    }

    /** Notify alarm server and other clients about updated PV configuration
     *  in RDB.
     *  @param path Path name of the modified item or <code>null</code> if all changed
     */
    public void sendConfigUpdate(final String path)
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    final MapMessage map = createMapMessage(
                                                JMSAlarmMessage.TEXT_CONFIG);
                    if (path != null)
                        map.setString(JMSLogMessage.NAME, path);
                    producer.send(map);
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error(ex);
                }
            }
        });
    }

    /** Send 'debug' message to server */
    public void triggerDebugAction()
    {
        queueJMSCommunication(new Runnable()
        {
            public void run()
            {
                try
                {
                    final MapMessage map = createMapMessage(
                                                JMSAlarmMessage.TEXT_DEBUG);
                    producer.send(map);
                }
                catch (Exception ex)
                {
                    CentralLogger.getInstance().getLogger(this).error(ex);
                }
            }
        });
    }
    
    /** Invoked for received messages */
    @Override
    protected void handleMapMessage(MapMessage message)
    {
        try
        {
            final String name = message.getString(JMSLogMessage.NAME);
            final String text = message.getString(JMSLogMessage.TEXT);
            Runnable action = null;

            // Create action, or handle the message right away
            
            // Alarm state change?
            if (JMSAlarmMessage.TEXT_STATE.equals(text))
            {
                // Received a state update from server, reset timeout
                timeout_timer.reset();
                action = createAlarmUpdateAction(message, name);
                model.updateServerState(false);
            }
            else if (JMSAlarmMessage.TEXT_STATE_MAINTENANCE.equals(text))
            {
                timeout_timer.reset();
                action = createAlarmUpdateAction(message, name);
                model.updateServerState(true);
            }
            // Idle messages in absence of 'real' traffic?
            else if (JMSAlarmMessage.TEXT_IDLE.equals(text))
            {
                timeout_timer.reset();
                model.updateServerState(false);
            }
            else if (JMSAlarmMessage.TEXT_IDLE_MAINTENANCE.equals(text))
            {
                timeout_timer.reset();
                model.updateServerState(true);
            }
            // Enable/disable?
            else if (JMSAlarmMessage.TEXT_ENABLE.equals(text))
            {
                timeout_timer.reset();
                action = new EnableAction(name, true);
            }
            else if (JMSAlarmMessage.TEXT_DISABLE.equals(text))
            {
                timeout_timer.reset();
                action = new EnableAction(name, false);
            }
            // Configuration change
            else if (JMSAlarmMessage.TEXT_CONFIG.equals(text))
                model.readConfig(name);
            // Debug trigger
            else if (JMSAlarmMessage.TEXT_DEBUG.equals(text))
                model.dump();

            if (action == null)
                return;
            // Queue or dispatch?
            synchronized (queue)
            {
                if (use_queue)
                {
                    queue.add(action);
                    return;
                }
            }
            // else: Not using queue, and queue no longer locked
            action.run();
        }
        catch (Exception ex)
        {
            CentralLogger.getInstance().getLogger(this).error(ex);
        }
    }

    /** Create an <code>UpdateAction</code> for an alarm state change
     *  @param message Message that contains info about state change
     *  @param name Alarm PV
     *  @return UpdateAction
     *  @throws Exception on error
     */
    private Runnable createAlarmUpdateAction(final MapMessage message, final String name)
            throws Exception
    {
        final SeverityLevel severity = SeverityLevel.parse(
                message.getString(JMSLogMessage.SEVERITY));
        final String status = message.getString(JMSAlarmMessage.STATUS);
        final SeverityLevel current_severity = SeverityLevel.parse(
                message.getString(JMSAlarmMessage.CURRENT_SEVERITY));
        final String current_message = message.getString(JMSAlarmMessage.CURRENT_STATUS);
        final String value = message.getString(JMSAlarmMessage.VALUE);
        final String timetext = message.getString(JMSLogMessage.EVENTTIME);
        final long millisecs = date_format.parse(timetext).getTime();
        final ITimestamp timestamp = TimestampFactory.fromMillisecs(millisecs);
        return new UpdateAction(name, current_severity, current_message,
                severity, status, value, timestamp);
    }
}
