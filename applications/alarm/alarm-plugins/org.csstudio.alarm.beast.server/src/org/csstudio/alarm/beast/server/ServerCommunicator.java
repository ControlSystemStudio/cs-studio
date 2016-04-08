/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.logging.Level;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;

import org.csstudio.alarm.beast.JMSAlarmMessage;
import org.csstudio.alarm.beast.JMSCommunicationWorkQueueThread;
import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.TimeoutTimer;
import org.csstudio.alarm.beast.WorkQueue;
import org.csstudio.logging.JMSLogMessage;
import org.diirt.util.time.Timestamp;

/** Communicates alarm system updates between server and clients.
 *  @author Kay Kasemir
 *  @author Jaka Bobnar - RDB batching
 */
@SuppressWarnings("nls")
public class ServerCommunicator extends JMSCommunicationWorkQueueThread
{
    /** TYPE identifier used for talk messages */
    private static final String TYPE_TALK = "talk";

    /** Format of time stamps */
    final private SimpleDateFormat date_format =
        new SimpleDateFormat(JMSLogMessage.DATE_FORMAT);

    /** Server for which we communicate */
    final private AlarmServer server;

    /** Work queue in main application */
    final private WorkQueue work_queue;

    /** Alarm tree root (config) name */
    final private String root_name;

    /** Timer for sending idle messages */
    final private TimeoutTimer idle_timer;

    /** Host for messages */
    final private String host = InetAddress.getLocalHost().getHostName();

    /** User for messages. Updated with authenticated user */
    final private String user = System.getProperty("user.name"); //$NON-NLS-1$

    // Note on synchronization:
    //
    // Access to the producer is within the JMSCommunicationThread
    // that also creates and closes them,
    // so there is no need to synch' on them.

    /** Producer for sending to the 'server' topic */
    private MessageProducer server_producer;

    /** Producer for sending to the 'talk' topic */
    private MessageProducer talk_producer;

    /** Producer for sending to the 'global' topic */
    private MessageProducer global_producer;

    /** Consumer for listening to the 'client' topic */
    private MessageConsumer client_consumer;

    /** Initialize communicator that writes to the 'server' topic
     *  and listens to 'client' topic messages
     *  @param server Alarm server
     *  @param work_queue
     *  @param root_name
     */
    public ServerCommunicator(final AlarmServer server, final WorkQueue work_queue,
            final String root_name) throws Exception
    {
        super(Preferences.getJMS_URL());
        this.server = server;
        this.work_queue = work_queue;
        this.root_name = root_name;
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

    // JMSCommunicationThread
    @Override
    protected void createProducersAndConsumers() throws Exception
    {
        // Send initial CONFIG message to client topic.
        // This will cause all running clients to re-load the configuration,
        // asserting that server which starts up now and clients
        // are in sync in case the RDB was changed.
        final MessageProducer client_producer = createProducer(Preferences.getJMS_AlarmClientTopic(root_name));
        try
        {
            final MapMessage map = createAlarmMessage(JMSAlarmMessage.TEXT_CONFIG);
            client_producer.send(map);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "Cannot send idle message", ex);
        }
        finally
        {
            client_producer.close();
        }

        server_producer = createProducer(Preferences.getJMS_AlarmServerTopic(root_name));
        talk_producer = createProducer(Preferences.getJMS_TalkTopic(root_name));
        global_producer = createProducer(Preferences.getJMS_GlobalServerTopic());
        client_consumer = createConsumer(Preferences.getJMS_AlarmClientTopic(root_name));
        client_consumer.setMessageListener(new MessageListener()
        {
            @Override
            public void onMessage(final Message message)
            {
                if (message instanceof MapMessage)
                    handleMapMessage((MapMessage) message);
                else
                    Activator.getLogger().log(Level.WARNING, "Message type {0} not handled", message.getClass().getName());
            }
        });
    }

    // JMSCommunicationThread
    @Override
    protected void closeProducersAndConsumers() throws Exception
    {
        client_consumer.close();
        client_consumer = null;
        global_producer.close();
        global_producer = null;
        talk_producer.close();
        talk_producer = null;
        server_producer.close();
        server_producer = null;
    }

    /** Start the communicator */
    @Override
    public void start()
    {
        super.start();
        idle_timer.start();
    }

    /** Stop the communicator */
    @Override
    public void stop()
    {
        idle_timer.cancel();
        try
        {
            idle_timer.join();
        }
        catch (InterruptedException ex)
        {
            Activator.getLogger().log(Level.WARNING, "Idle Timer join failed", ex);
        }
        super.stop();
    }

    /** Create message initialized with basic alarm & application info
     *  @param text TEXT property
     *  @return MapMessage
     *  @throws Exception on error.
     */
    private MapMessage createAlarmMessage(final String text) throws Exception
    {
        return createMessage(JMSAlarmMessage.TYPE_ALARM, text);
    }

    /** Create message initialized with basic alarm & application info
     *  @param text TEXT property
     *  @return MapMessage
     *  @throws Exception on error.
     */
    private MapMessage createMessage(final String type, final String text) throws Exception
    {
        final MapMessage map = createMapMessage();
        map.setString(JMSLogMessage.TYPE, type);
        map.setString(JMSAlarmMessage.CONFIG, server.getRootName());
        map.setString(JMSLogMessage.TEXT, text);
        map.setString(JMSLogMessage.APPLICATION_ID, Application.APPLICATION_NAME);
        map.setString(JMSLogMessage.HOST, host);
        map.setString(JMSLogMessage.USER, user);
        return map;
    }

    /** Send idle message, which includes the operating state.
     *  This is usually invoked by the idle_timer,
     *  but can be invoked on purpose to update clients in mode changes ASAP.
     */
    protected void sendIdleMessage()
    {
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createAlarmMessage(
                           AlarmLogic.getMaintenanceMode()
                           ? JMSAlarmMessage.TEXT_IDLE_MAINTENANCE
                           : JMSAlarmMessage.TEXT_IDLE);
                    server_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Cannot send idle message", ex);
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
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createAlarmMessage(JMSAlarmMessage.TEXT_CONFIG);
                    server_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Cannot send reload message", ex);
                }
            }
        });
        idle_timer.reset();
    }

    /** Notify clients of new alarm state.
     *  @param pv PV that changes alarm state
     *  @param current_severity Current severity of the PV
     *  @param current_message Current message of the PV
     *  @param alarm_severity Alarm severity
     *  @param alarm_message Alarm message
     *  @param value Value that triggered update
     *  @param timestamp Instant stamp for alarm severity/status
     */
    protected void sendStateUpdate(final AlarmPV pv,
            final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel alarm_severity, final String alarm_message,
            final String value,
            final Instant timestamp)
    {
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createAlarmMessage(
                            AlarmLogic.getMaintenanceMode()
                            ? JMSAlarmMessage.TEXT_STATE_MAINTENANCE
                            : JMSAlarmMessage.TEXT_STATE);
                    map.setString(JMSLogMessage.NAME, pv.getName());
                    map.setString(JMSLogMessage.SEVERITY, alarm_severity.name());
                    map.setString(JMSAlarmMessage.STATUS,  alarm_message);
                    if (value != null)
                        map.setString(JMSAlarmMessage.VALUE, value);
                    map.setString(JMSAlarmMessage.EVENTTIME, date_format.format(timestamp.toDate()));
                    map.setString(JMSAlarmMessage.CURRENT_SEVERITY, current_severity.name());
                    map.setString(JMSAlarmMessage.CURRENT_STATUS, current_message);
                    server_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Cannot send state update message", ex);
                }
            }
        });
        idle_timer.reset();
    }

    /** Notify 'global' clients of new alarm state.
     *  @param pv PV that changes alarm state
     *  @param alarm_severity Alarm severity
     *  @param alarm_message Alarm message
     *  @param value Value that triggered update
     *  @param timestamp Instant for alarm severity/status
     */
    protected void sendGlobalUpdate(final AlarmPV pv,
            final SeverityLevel alarm_severity, final String alarm_message,
            final String value,
            final Instant timestamp)
    {
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createAlarmMessage(JMSAlarmMessage.TEXT_STATE);
                    map.setString(JMSLogMessage.NAME, pv.getPathName());
                    map.setString(JMSLogMessage.SEVERITY, alarm_severity.name());
                    map.setString(JMSAlarmMessage.STATUS,  alarm_message);
                    if (value != null)
                        map.setString(JMSAlarmMessage.VALUE, value);
                    map.setString(JMSAlarmMessage.EVENTTIME, date_format.format(timestamp.toDate()));
                    global_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Cannot send global update", ex);
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
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                final String text = enabled ? JMSAlarmMessage.TEXT_ENABLE
                                            : JMSAlarmMessage.TEXT_DISABLE;
                try
                {
                    final MapMessage map = createAlarmMessage(text);
                    map.setString(JMSLogMessage.NAME, pv.getName());
                    server_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.WARNING, "Cannot send enablement update", ex);
                }
            }
        });
        idle_timer.reset();
    }

    /** Send message to annunciator
     *  @param message Message text
     */
    public void sendAnnunciation(final String message)
    {
        sendAnnunciation (null, message);
    }

    /** Send message to annunciator
     *  @param level Severity Level or <code>null</code>
     *  @param message Message text
     */
    public void sendAnnunciation(final SeverityLevel level, final String message)
    {
        execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final MapMessage map = createMessage(TYPE_TALK, message);
                    if (level != null)
                        map.setString(JMSLogMessage.SEVERITY, level.name());
                    talk_producer.send(map);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().log(Level.SEVERE, "Annunciation failed", ex);
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
    private void handleMapMessage(final MapMessage message)
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
                work_queue.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            server.updateConfig(name);
                        }
                        catch (Throwable ex)
                        {
                            Activator.getLogger().log(Level.SEVERE, "Config update error", ex);
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
                    Activator.getLogger().log(Level.WARNING, "Unknown MODE request {0}", mode);
            }
            else if (JMSAlarmMessage.TEXT_DEBUG.equals(text))
                server.dump(System.out);
        }
        catch (Throwable ex)
        {
            Activator.getLogger().log(Level.SEVERE, "Message handler error", ex);
        }
    }
}
