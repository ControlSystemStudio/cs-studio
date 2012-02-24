
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

package org.csstudio.ams.delivery.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 23.02.2012
 */
public class JmsDeliveryWorker extends AbstractDeliveryWorker {
    
    private static final Logger LOG = LoggerFactory.getLogger(JmsDeliveryActivator.class);
    
    private JmsDevice jmsDevice;
    
    private boolean running;

    private boolean workerCheckFlag;

    public JmsDeliveryWorker() {
        workerName = this.getClass().getSimpleName();
        running = true;
        workerCheckFlag = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        //final IPreferenceStore prefs = AmsActivator.getDefault().getPreferenceStore();

        String publishUrl = prefs.getString(AmsActivator.PLUGIN_ID,
                                            AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                                            "tcp://localhost:62616",
                                            null);
        
        ConnectionFactory senderConnectionFactory = null;
        Connection senderConnection = null;
        Connection[] receiverConnections = null;
        
        try {
            senderConnectionFactory = new ActiveMQConnectionFactory(publishUrl);
            senderConnection = senderConnectionFactory.createConnection();
            senderConnection.start();
            final String[] receiverURLs = new String[] {
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                    "tcp://localhost:62616",
                                    null),
                    prefs.getString(AmsActivator.PLUGIN_ID,
                                    AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                    "tcp://localhost:64616",
                                    null)
                };
            receiverConnections = new Connection[receiverURLs.length];
            for (int i = 0; i < receiverURLs.length; i++) {
                final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(receiverURLs[i]);
                final Connection connection = connectionFactory.createConnection();
                receiverConnections[i] = connection;
                final Session receiverSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String topicName = prefs.getString(AmsActivator.PLUGIN_ID,
                                                   AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR,
                                                   "T_AMS_CON_JMS",
                                                   null);
                final Topic receiveTopic = receiverSession.createTopic(topicName);
                final MessageConsumer consumer = receiverSession.createConsumer(receiveTopic);
    
                // Create a new sender session for each worker because each worker will be called in its own listener thread
                final Session senderSession = senderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    
                jmsDevice = new JmsDevice(senderSession);
                consumer.setMessageListener(jmsDevice);
                connection.start();
            }
        } catch (JMSException jmse) {
            LOG.error("[*** JMSException ***]: Cannot initialize {}: {}", workerName, jmse.getMessage());
            LOG.error("Leaving worker.");
            running = false;
        }
        
        while (running) {
            synchronized (this) {
                try {
                    this.wait();
                    workerCheckFlag = false;
                } catch (InterruptedException e) {
                    LOG.warn("I have been interrupted.");
                }
            }
        }
        
        if (jmsDevice != null) {
            jmsDevice.stopDevice();
        }
        
        if (receiverConnections != null) {
            for (final Connection receiverConnection : receiverConnections) {
                try {
                    receiverConnection.stop();
                } catch (JMSException e) {
                    e.printStackTrace();
                }

                try {
                    receiverConnection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if (senderConnection != null) {
            try {
                senderConnection.stop();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            try {
                senderConnection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        
        LOG.info("{} is exiting now.", workerName);
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
        workerCheckFlag = true;
        synchronized (this) {
            this.notify();
        }
        final Object localLock = new Object();
        synchronized (localLock) {
            try {
                localLock.wait(250);
            } catch (final InterruptedException e) {
                // Ignore me
            }
        }
        return !workerCheckFlag;
    }
}
