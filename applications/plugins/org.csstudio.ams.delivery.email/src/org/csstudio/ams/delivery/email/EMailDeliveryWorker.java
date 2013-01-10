
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

package org.csstudio.ams.delivery.email;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.util.jms.JmsAsyncConsumer;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 10.12.2011
 */
public class EMailDeliveryWorker extends AbstractDeliveryWorker {
    
    public final static int STAT_INIT = 0;
    public final static int STAT_OK = 1;
    public final static int STAT_ERR_EMAIL = 2;
    public final static int STAT_ERR_EMAIL_SEND = 3;
    public final static int STAT_ERR_JMSCON = 4; // jms communication to ams internal jms partners
    public final static int STAT_ERR_UNKNOWN = 5;

    private static final Logger LOG = LoggerFactory.getLogger(EMailDeliveryWorker.class);
    
    private JmsAsyncConsumer amsConsumer;
    
    private OutgoingEMailQueue messageQueue;
    
    private EMailWorkerProperties emailProps;
    
    private EMailDevice mailDevice;
    
    private EMailWorkerStatus workerStatus;
    
    private boolean running;
    
    /**
     * Constructor.
     */
    public EMailDeliveryWorker() {
        setWorkerName(this.getClass().getSimpleName());
        emailProps = new EMailWorkerProperties();
        messageQueue = new OutgoingEMailQueue(this, emailProps);
        workerStatus = new EMailWorkerStatus();
        mailDevice = new EMailDevice(emailProps, workerStatus);
        running = true;
        initJms();
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

            LOG.info("Number of messages to send: " + messageQueue.size());
            while (messageQueue.hasContent()) {
                final EMailAlarmMessage outMsg = messageQueue.nextMessage();
                if (mailDevice.sendMessage(outMsg) == false) {
                    if (outMsg.getMessageState() == State.FAILED) {
                        LOG.warn("Cannot send message: {}", outMsg);
                        messageQueue.addMessage(outMsg);
                        LOG.warn("Re-Insert it into the message queue.");
                    } else {
                        // TODO: Handle the messages with the state BAD!
                        LOG.warn("Dicarding message: {}", outMsg);
                    }
                }
            }
        }

        mailDevice.stopDevice();
        closeJms();
        
        LOG.info("{} is leaving.", workerName);
    }
    
    private boolean initJms() {
        
        boolean result = false;

        try  {
            IPreferencesService prefs = Platform.getPreferencesService();
            
            String value = prefs.getString(AmsActivator.PLUGIN_ID,
                                           AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE,
                                           "false",
                                           null);
            final boolean durable = Boolean.parseBoolean(value);

            final String url1 = prefs.getString(AmsActivator.PLUGIN_ID,
                                                AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                                "tcp://localhost:62616",
                                                null);
            final String url2 = prefs.getString(AmsActivator.PLUGIN_ID,
                                                AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                                "tcp://localhost:64616",
                                                null);
            LOG.info("Connecting for urls: " + url1 + " and " + url2);
            amsConsumer = new JmsAsyncConsumer("EMailConnectorWorkReceiverInternal",
                                               url1,
                                               url2);

            if(!amsConsumer.isConnected()) {
                LOG.error("Could not create amsConsumer");
                return false;
            }

            result = amsConsumer.createRedundantSubscriber(
                    "amsSubscriberEmail",
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR,
                                    "T_AMS_CON_MAIL",
                                    null),
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_TSUB_EMAIL_CONNECTOR,
                                    "SUB_AMS_CON_MAIL",
                                    null),
                    durable);
            if(result == false) {
                LOG.error("Could not create amsSubscriberEmail");
                return false;
            }

            amsConsumer.addMessageListener("amsSubscriberEmail", messageQueue);
            return true;
        } catch(final Exception e) {
            LOG.error("Could not init internal Jms: {}", e.getMessage());
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
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public boolean isWorking() {
        return workerStatus.isOk();
    }
}
