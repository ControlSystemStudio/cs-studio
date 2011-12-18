
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.delivery.sms;

import java.util.ArrayList;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.ams.delivery.jms.JmsAsyncConsumer;
import org.csstudio.ams.delivery.jms.JmsSender;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 17.12.2011
 */
public class SmsDeliveryWorker extends AbstractDeliveryWorker {
    
    private static final Logger LOG = LoggerFactory.getLogger(SmsDeliveryWorker.class);
    
    /** Name of the management topic for sending commands */
    private static final String MANAGE_COMMAND_TOPIC = "T_AMS_CON_MANAGE";
    private static final String MANAGE_COMMAND_TOPIC_SUB = "T_AMS_TSUB_CON_MANAGE";

    private JmsAsyncConsumer amsConsumer;
    
    /** --- Sender --- */
    private JmsSimpleProducer amsPublisherReply;

    private OutgoingSmsQueue messageQueue;
    
    private SmsDeliveryDevice smsDevice;
    
    private boolean running;
    
    /**
     * Constructor.
     */
    public SmsDeliveryWorker() {
        workerName = this.getClass().getSimpleName();
        messageQueue = new OutgoingSmsQueue();
        smsDevice = new SmsDeliveryDevice();
        running = true;
        initJms();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        
        ArrayList<SmsAlarmMessage> outgoing = null;
        
        LOG.info(workerName + " is running.");
                
        while(running) {
            synchronized (messageQueue) {
                try {
                    messageQueue.wait();
                } catch (InterruptedException ie) {
                    LOG.error("I have been interrupted.");
                }
                
                outgoing = messageQueue.getCurrentContent();
                LOG.info("zu senden: " + outgoing.size());
            }
            
            for (SmsAlarmMessage o : outgoing) {
                try {
                    smsDevice.sendMessage(o);
                } catch (Exception e) {
                    LOG.error("Cannot send message: {}", o);
                    messageQueue.addMessage(o);
                    LOG.error("Re-Insert it into the message queue.");
                }
            }
            
            outgoing.clear();
            outgoing = null;
        }

        closeJms();
        smsDevice.stopDevice();
        
        LOG.info(workerName + " is leaving.");
    }
    
    private boolean initJms() {
        
        boolean result = false;
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String factoryClass = prefs.getString(AmsActivator.PLUGIN_ID,
                                              AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS,
                                              "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                                              null);
        String url = prefs.getString(AmsActivator.PLUGIN_ID,
                                     AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                                     "failover:(tcp://localhost:62616,tcp://localhost:64616)",
                                     null);
        String topic = prefs.getString(AmsActivator.PLUGIN_ID,
                                       AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY,
                                       "T_AMS_CON_REPLY",
                                       null);
        
        amsPublisherReply = new JmsSimpleProducer("SmsConnectorWorkSenderInternal", url,
                                                  factoryClass, topic);
        if (amsPublisherReply == null) {
            Log.log(this, Log.FATAL, "Could not create amsPublisherReply");
            return false;
        }

        try {
            
            String value = prefs.getString(AmsActivator.PLUGIN_ID,
                                           AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE,
                                           "false",
                                           null);
            final boolean durable = Boolean.parseBoolean(value);

            // Create the redundant receiver
            amsConsumer = new JmsAsyncConsumer("SmsConnectorWorkReceiverInternal",
                                               prefs.getString(AmsActivator.PLUGIN_ID,
                                                               AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                                               "",
                                                               null),
                                               prefs.getString(AmsActivator.PLUGIN_ID,
                                                               AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                                               "",
                                                               null));
           
            // Create first subscriber (default topic for the connector) 
            result = amsConsumer.createRedundantSubscriber(
                    "amsSubscriberSms",
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR,
                                    "T_AMS_CON_SMS",
                                    null),
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR,
                                    "SUB_AMS_CON_SMS",
                                    null),
                    durable);
            if(result == false) {
                Log.log(this, Log.FATAL, "could not create amsSubscriberSms");
                return false;
            }
            
            // Create second subscriber (topic for the modem test) 
            result = amsConsumer.createRedundantSubscriber(
                    "amsSubscriberSmsModemtest",
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TOPIC_CONNECTOR_DEVICETEST,
                                    "T_AMS_CON_DEVICETEST",
                                    null),
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR_DEVICETEST,
                                    "SUB_AMS_CON_SMS_DEVICETEST",
                                    null),
                    durable);
            if(result == false) {
                Log.log(this, Log.FATAL, "could not create amsSubscriberSmsModemtest");
                return false;
            }

            // Create third subscriber (topic for message management)
            // TODO: Replace constant with preference entry 
            result = amsConsumer.createRedundantSubscriber(
                    "amsConnectorManager",
                    MANAGE_COMMAND_TOPIC,
                    MANAGE_COMMAND_TOPIC_SUB,
                    durable);
            if(result == false)  {
                Log.log(this, Log.FATAL, "could not create amsConnectorManager");
                return false;
            }

            return true;
        } catch(Exception e) {
            Log.log(this, Log.FATAL, "could not init internal Jms", e);
            
            JmsSender sender = new JmsSender("SmsConnectorAlarmSender",
                                             prefs.getString(AmsActivator.PLUGIN_ID,
                                                             AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                                                             "failover:(tcp://localhost:62616,tcp://localhost:64616)",
                                                             null),
                                             "ALARM");
            if(sender.isConnected()) {
                if(sender.sendMessage("alarm",
                                      "SmsConnectorWork: Cannot init internal Jms [" + e.getMessage() + "]",
                                      "MAJOR") == false) {
                    Log.log(this, Log.ERROR, "Cannot send alarm message.");
                }  else {
                    Log.log(this, Log.INFO, "Alarm message sent.");
                }
            } else {
                Log.log(this, Log.WARN, "Alarm message sender is NOT connected.");
            }
            
            sender.closeAll();
            sender = null;
        }
        
        return false;
    }

    private void closeJms() {
        if (amsConsumer != null) {
            amsConsumer.closeAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopWorking() {
        running = false;
        synchronized (messageQueue) {
            messageQueue.notify();
        }
    }
}
