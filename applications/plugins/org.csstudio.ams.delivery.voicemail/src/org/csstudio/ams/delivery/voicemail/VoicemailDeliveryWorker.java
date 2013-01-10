
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.delivery.voicemail;

import java.util.List;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.ams.delivery.device.DeviceException;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.util.jms.JmsAsyncConsumer;
import org.csstudio.ams.delivery.voicemail.isdn.VoicemailDevice;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 07.02.2012
 */
public class VoicemailDeliveryWorker extends AbstractDeliveryWorker {
    
    private static final Logger LOG = LoggerFactory.getLogger(VoicemailDeliveryWorker.class);
    
    /** The producer sends messages to topic T_AMS_CON_REPLY */
    private JmsSimpleProducer amsPublisherReply;
    
    /** The consumer that listens to topic T_AMS_CONNECTOR_VOICEMAIL */
    private JmsAsyncConsumer amsConsumer; 
    
    private OutgoingVoicemailQueue messageQueue;
    
    private VoicemailDevice device;
    
    private VoicemailWorkerStatus workerStatus;
    
    private boolean running;

    public VoicemailDeliveryWorker() {
        workerName = this.getClass().getSimpleName();
        messageQueue = new OutgoingVoicemailQueue(this);
        running = initJms();
        workerStatus = new VoicemailWorkerStatus();
        if (running) {
            try {
                device = new VoicemailDevice(workerStatus);
            } catch (DeviceException e) {
                LOG.error("Cannot initialize the device: ISDN-Modem: {}", e.getMessage());
                running = false;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        
        LOG.info(workerName + " is running.");

        while(running) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException ie) {
                    LOG.error("I have been interrupted.");
                }
            }

            int sent = 0;
            while (messageQueue.hasContent()) {
                // Get all messages and remove them
                List<VoicemailAlarmMessage> outgoing = messageQueue.getCurrentContent();
                LOG.info("Number of messages to send: " + outgoing.size());
                
                sent = device.sendMessages(outgoing);
                if (sent < outgoing.size()) {
                    for (VoicemailAlarmMessage o : outgoing) {
                        if (o.getMessageState() == State.FAILED) {
                            messageQueue.addMessage(o);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stopWorking() {
        running = false;
        synchronized (this) {
            this.notify();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWorking() {
        return workerStatus.isOk();
    }
    
    private boolean initJms() {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        boolean success = false;
        
        boolean durable = prefs.getBoolean(AmsActivator.PLUGIN_ID,
                                       AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE,
                                       false,
                                       null);
        
        String url = prefs.getString(AmsActivator.PLUGIN_ID,
                                     AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                     "tcp://localhost:62616",
                                     null);
        String topic = prefs.getString(AmsActivator.PLUGIN_ID,
                                       AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY,
                                       "T_AMS_CON_REPLY",
                                       null);
        String factoryClass = prefs.getString(AmsActivator.PLUGIN_ID,
                                              AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS,
                                              "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                                              null);
        
        amsPublisherReply = new JmsSimpleProducer("VoicemailConnectorWorkSenderInternal", url, factoryClass, topic);
        if (amsPublisherReply.isConnected() == false) {
            LOG.error("Could not create amsPublisherReply");
            return false;
        }
        
        try {
            
            String url1 = prefs.getString(AmsActivator.PLUGIN_ID,
                                          AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                          "tcp://localhost:62616",
                                          null);
            String url2 = prefs.getString(AmsActivator.PLUGIN_ID,
                                          AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                          "tcp://localhost:64616",
                                          null);
            amsConsumer = new JmsAsyncConsumer("VoicemailConnectorWorkReceiverInternal", url1, url2);
            success = amsConsumer.createRedundantSubscriber(
                    "amsSubscriberVm",
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR,
                                    "T_AMS_CON_VOICEMAIL",
                                    null),
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR,
                                    "SUB_AMS_CON_VOICEMAIL",
                                    null),
                    durable);
            
            if(success == false) {
                LOG.error("Could not create amsSubscriberVm");
            }
            amsConsumer.addMessageListener("amsSubscriberVm", messageQueue);
        } catch(Exception e) {
            LOG.error("Could not init internal Jms: {}", e.getMessage());
        }
        
        return success;
    }
}
