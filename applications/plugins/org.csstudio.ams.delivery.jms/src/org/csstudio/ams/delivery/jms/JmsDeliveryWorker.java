
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

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.ams.delivery.jms.internal.JmsDeliveryPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.utility.jms.IConnectionMonitor;
import org.csstudio.utility.jms.JmsUtilityException;
import org.csstudio.utility.jms.TransportEvent;
import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 23.02.2012
 */
public class JmsDeliveryWorker extends AbstractDeliveryWorker implements IConnectionMonitor {
    
    private static final Logger LOG = LoggerFactory.getLogger(JmsDeliveryActivator.class);
    
    private JmsDevice jmsDevice;
    
    private JmsWorkerStatus workerStatus;
    
    private boolean running;

    public JmsDeliveryWorker() {
        workerName = this.getClass().getSimpleName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        LOG.info(workerName + " is running.");

        IPreferencesService prefs = Platform.getPreferencesService();
        
        long maxReceivDiff = prefs.getLong(JmsDeliveryActivator.PLUGIN_ID,
                                           JmsDeliveryPreferenceKey.P_MAX_ALLOWED_RECEIVING_DIFF,
                                           300000L,
                                           null);
        LOG.info("Max. time diff for JMS receiving: {}", maxReceivDiff);
        workerStatus = new JmsWorkerStatus(maxReceivDiff);
        running = true;

        // Create the JMS publisher
        ISharedConnectionHandle publisherHandle = null;
        Session publisherSession = null;
        
        ISharedConnectionHandle[] consumerHandles = null;
        Session[] consumerSession = null;
        MessageConsumer[] consumer = null;
        
        String consumerTopicName = prefs.getString(AmsActivator.PLUGIN_ID,
                                           AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR,
                                           "T_AMS_CON_JMS",
                                           null);
        try {

            publisherHandle = SharedJmsConnections.sharedSenderConnection();
            publisherSession = publisherHandle.createSession(false, Session.AUTO_ACKNOWLEDGE);
            jmsDevice = new JmsDevice(publisherSession, workerStatus);

            consumerHandles = SharedJmsConnections.sharedReceiverConnections();
            consumerSession = new Session[consumerHandles.length];
            consumer = new MessageConsumer[consumerHandles.length];
            
            for (int i = 0; i < consumerHandles.length; i++) {
                consumerHandles[i].addMonitor(this);
                consumerSession[i] = consumerHandles[i].createSession(false, Session.AUTO_ACKNOWLEDGE);
                final Topic receiveTopic = consumerSession[i].createTopic(consumerTopicName);
                consumer[i] = consumerSession[i].createConsumer(receiveTopic);
                consumer[i].setMessageListener(jmsDevice);
            }
        } catch (JMSException jmse) {
            LOG.error("[*** JMSException ***]: Cannot initialize {}: {}", workerName, jmse.getMessage());
            LOG.error("Leaving worker.");
            running = false;
        } catch (JmsUtilityException jue) {
            LOG.error("[*** JmsUtilityException ***]: Cannot initialize {}: {}", workerName, jue.getMessage());
            LOG.error("Leaving worker.");
            running = false;
        }
        
        while (running) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    LOG.warn("I have been interrupted.");
                }
            }
        }
        
        if (jmsDevice != null) {
            jmsDevice.stopDevice();
        }
        
        if (consumer != null) {
            for (MessageConsumer o : consumer) {
                if (o != null) {
                    try {o.close();}catch(JMSException e){/* Ignore me */}
                }
            }
        }
        
        if (consumerSession != null) {
            for (Session o : consumerSession) {
                if (o != null) {
                    try {o.close();}catch(JMSException e){/* Ignore me */}
                }
            }
        }

        if (consumerHandles != null) {
            for (ISharedConnectionHandle o : consumerHandles) {
                if (o != null) {
                    o.release();
                }
            }
        }
        
        if (publisherSession != null) {
            try {publisherSession.close();}catch(JMSException e){/* Ignore me */}
        }
        
        if (publisherHandle != null) {
            publisherHandle.release();
        }
        
        LOG.info("{} is leaving.", workerName);
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
    public synchronized boolean isWorking() {
        boolean isWorking = false;
        if (workerStatus != null) {
            isWorking = workerStatus.isOk();
        }
        return isWorking;
    }

    @Override
    public void onConnected(TransportEvent event) {
        LOG.warn("onConnected(): {}", event);
    }

    @Override
    public void onDisconnected(TransportEvent event) {
        LOG.warn("onDisconnected(): {}", event);
    }
}
