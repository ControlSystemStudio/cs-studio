/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.clientmodel;

import java.net.InetAddress;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.logging.Level;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.security.auth.Subject;

import org.csstudio.alarm.beast.Activator;
import org.csstudio.alarm.beast.JMSAlarmMessage;
import org.csstudio.alarm.beast.JMSCommunicationWorkQueueThread;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.TimeoutTimer;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.security.SecuritySupport;

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
@SuppressWarnings("nls")
class AlarmClientCommunicator extends JMSCommunicationWorkQueueThread
{
    /** Application name used when sending JMS messages */
    final private static String APPLICATION = "CSS";

    /** The date format for converting the received timestamp info into a Date object */
    private final DateFormat date_format = new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);

    /** The model */
    final private AlarmClientModel model;

    /** Action to update a PV's state */
    private class UpdateAction implements Runnable
    {
        final private AlarmUpdateInfo info;

        public UpdateAction(final AlarmUpdateInfo info)
        {
            this.info = info;
        }

        @Override
        public void run()
        {
            model.updatePV(info);
        }

        @Override
        public String toString()
        {
            return info.toString();
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

        @Override
        public void run()
        {
            model.updateEnablement(name, enable);
        }

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

    /** Event queue (filled in Queue mode) */
    private WorkQueue queue = new WorkQueue();

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

    /** JMS producer for messages from client to server (<code>null</code> when read-only) */
    private MessageProducer client_producer;

    /** JMS read-back of client messages */
    private MessageConsumer client_consumer;

    /** JMS consumer for messages from server to client */
    private MessageConsumer server_consumer;

    /** Host for messages */
    final private String host = InetAddress.getLocalHost().getHostName();

    /** User for messages. Updated with authenticated user */
    private String user = System.getProperty("user.name");

    /** Initialize communicator that writes to the 'client' topic
     *  and listens to 'server' topic
     *  @param model Model for which to communicate
     *  @throws Exception on error
     */
    public AlarmClientCommunicator(final AlarmClientModel model) throws Exception
    {
        super(Preferences.getJMS_URL());
        this.model = model;
        timeout_timer.start();
    }

    // JMSCommunicationThread
    @Override
    protected void createProducersAndConsumers() throws Exception
    {
        final String configuration = model.getConfigurationName();
        // Write (if allowed) and also read the client topic
        if (model.isWriteAllowed())
            client_producer = createProducer(Preferences.getJMS_AlarmClientTopic(configuration));
        else
            client_producer = null;
        client_consumer = createConsumer(Preferences.getJMS_AlarmClientTopic(configuration));
        // Read messages from server
        server_consumer = createConsumer(Preferences.getJMS_AlarmServerTopic(configuration));

        // Handle MapMessages
        final MessageListener message_listener = new MessageListener()
        {
            @Override
            public void onMessage(final Message message)
            {
                if (message instanceof MapMessage)
                    handleMapMessage((MapMessage) message);
                else
                    Activator.getLogger().log(Level.WARNING,
                            "Message type {0} not handled", message.getClass().getName());
            }
        };
        client_consumer.setMessageListener(message_listener);
        server_consumer.setMessageListener(message_listener);
    }

    // JMSCommunicationThread
    @Override
    protected void closeProducersAndConsumers() throws Exception
    {
        timeout_timer.cancel();
        server_consumer.close();
        server_consumer = null;
        client_consumer.close();
        client_consumer = null;
        if (client_producer != null)
        {
            client_producer.close();
            client_producer = null;
        }
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
        this.use_queue = use_queue;
        if (use_queue)
            return;
        // Queuing turned off -> Dispatched what has accumulated until now
        queue.performQueuedCommands();
    }

    /** Create message initialized with basic alarm & application info
     *  @param text TEXT property
     *  @return MapMessage
     *  @throws Exception on error.
     */
    private MapMessage createMapMessage(final String text) throws Exception
    {
        final MapMessage map = createMapMessage();
        map.setString(JMSLogMessage.TYPE, JMSAlarmMessage.TYPE_ALARM);
        map.setString(JMSAlarmMessage.EVENTTIME, date_format.format(Date.from(Instant.now())));
        map.setString(JMSAlarmMessage.CONFIG, model.getConfigurationName());
        map.setString(JMSLogMessage.TEXT, text);
        map.setString(JMSLogMessage.APPLICATION_ID, APPLICATION);
        map.setString(JMSLogMessage.HOST, host);

        final Subject subject = SecuritySupport.getSubject();
        if (subject == null)  //if no user logged in...
            user = System.getProperty("user.name"); //$NON-NLS-1$
        else
            user = SecuritySupport.getSubjectName(subject);
        map.setString(JMSLogMessage.USER, user);
        return map;
    }

    /** Send request to enable/disable maintenance mode to alarm server
     *  @param maintenance <code>true</code> to enable
     */
    public void requestMaintenanceMode(final boolean maintenance)
    {
        if (client_producer == null)
            return;
        execute(new Runnable()
        {
            @Override
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
                    client_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.SEVERE,
                            "Cannot request maintenance mode", ex);
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
        if (client_producer == null)
            return;
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createMapMessage(acknowledge
                            ? JMSAlarmMessage.TEXT_ACKNOWLEDGE
                            : JMSAlarmMessage.TEXT_UNACKNOWLEDGE);
                    map.setString(JMSAlarmMessage.CONFIG, pv.getPathName());
                    map.setString(JMSLogMessage.NAME, pv.getName());
                    client_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.SEVERE,
                            "Cannot request acknowledgement", ex);
                }
            }
        });
    }

    /** Notify alarm server and other clients about updated PV configuration
     *  in RDB.
     *  @param path Path name of a modified existing item, or <code>null</code> for new or removed item
     */
    public void sendConfigUpdate(final String path)
    {
        if (client_producer == null)
            return;
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createMapMessage(
                                                JMSAlarmMessage.TEXT_CONFIG);
                    if (path != null){
                        map.setString(JMSAlarmMessage.CONFIG, path);
                        map.setString(JMSLogMessage.NAME, path);
                    }
                    client_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.SEVERE,
                            "Cannot send config update", ex);
                }
            }
        });
    }

    /** Send 'debug' message to server */
    public void triggerDebugAction()
    {
        if (client_producer == null)
            return;
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createMapMessage(
                                                JMSAlarmMessage.TEXT_DEBUG);
                    client_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Debug trigger failed", ex);
                }
            }
        });
    }

    /** Invoked for received messages */
    private void handleMapMessage(final MapMessage message)
    {
        try
        {
            final String text = message.getString(JMSLogMessage.TEXT);
            Runnable action = null;

            // Create action, or handle the message right away

            // Alarm state change?
            if (JMSAlarmMessage.TEXT_STATE.equals(text))
            {
                // Received a state update from server, reset timeout
                timeout_timer.reset();
                action = new UpdateAction(AlarmUpdateInfo.fromMapMessage(message,date_format));
                model.updateServerState(false);
            }
            else if (JMSAlarmMessage.TEXT_STATE_MAINTENANCE.equals(text))
            {
                timeout_timer.reset();
                action = new UpdateAction(AlarmUpdateInfo.fromMapMessage(message,date_format));
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
                final String name = message.getString(JMSLogMessage.NAME);
                action = new EnableAction(name, true);
            }
            else if (JMSAlarmMessage.TEXT_DISABLE.equals(text))
            {
                timeout_timer.reset();
                final String name = message.getString(JMSLogMessage.NAME);
                action = new EnableAction(name, false);
            }
            // Configuration change
            else if (JMSAlarmMessage.TEXT_CONFIG.equals(text))
            {
                final String name = message.getString(JMSLogMessage.NAME);
                model.readConfig(name);
            }
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
                    queue.execute(action);
                    return;
                }
            }
            // else: Not using queue, and queue no longer locked
            action.run();
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Message handler error", ex);
        }
    }
}
