
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

package org.csstudio.ams.application.monitor.check;

import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.csstudio.ams.application.monitor.AmsMonitorException;
import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.csstudio.ams.application.monitor.message.DeliveryWorkerAnswerMessage;
import org.csstudio.ams.application.monitor.message.InitiatorMessage;
import org.csstudio.ams.application.monitor.message.MessageCreator.MessageType;
import org.csstudio.ams.application.monitor.status.CheckStatus;
import org.csstudio.ams.application.monitor.status.CheckStatusInfo;
import org.csstudio.ams.application.monitor.status.ErrorReason;
import org.csstudio.ams.application.monitor.status.MonitorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 13.06.2012
 */
public class NewSmsCheckProcessor extends AbstractCheckProcessor implements MessageListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(SmsCheckProcessor.class);
    
    //private Vector<DeliveryWorkerAnswerMessage> messageQueue;

    private ConcurrentLinkedQueue<DeliveryWorkerAnswerMessage> messageQueue;
    
    private long waitTime;

    public NewSmsCheckProcessor(String subscriberName, String ws) {
        this(subscriberName, ws, 0L);
    }
    
    public NewSmsCheckProcessor(String subscriberName, String ws, long interval) {
        
        super(LOG, ws, subscriberName, interval);
        
        messageQueue = new ConcurrentLinkedQueue<DeliveryWorkerAnswerMessage>();
        waitTime = AmsMonitorPreference.DELIVERY_WORKER_CHECK_WAIT_TIME.getValue();

        // Assume that the wait time has to be converted to ms
        waitTime *= 1000L;
        LOG.info("Wait time: {}", waitTime);
        
        if (!readMonitorStatus()) {
            monitorStatus = new MonitorStatus(AmsMonitorPreference.MAX_ALLOWED_WORKER_ERROR.getValue(),
                                              AmsMonitorPreference.MAX_ALLOWED_WORKER_WARN.getValue());
        } else {
            // If the last check was successful, reset the MonitorStatus
            // The last check result will be preserved
            CheckStatusInfo csi = monitorStatus.getCurrentCheckStatusInfo();
            if (csi != null) {
                if (csi.getCheckStatus() == CheckStatus.OK) {
                    monitorStatus.reset();
                }
            }
        }
        
        monitorStatus.createCurrentCheckStatusInfo();
        LOG.info(monitorStatus.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        
        hasBeenStarted = true;
        
        // Create a new initiator message for this check cycle (always a new message!)
        InitiatorMessage currentMessage = messageCreator.getInitiatorMessage(MessageType.SMS_DELIVERY_WORKER);
        
        // Remember the message if the answer will not return immediately
        monitorStatus.addMessage(currentMessage);
        
        CheckStatusInfo currentStatus = monitorStatus.getCurrentCheckStatusInfo();

        // Convert the message to a MapMessage object and send it
        try {
            
            initJms();

            // Start the asynchronous message receiving
            consumer.addMessageListener(this);
            
            MapMessage mapMessage = messageConverter.convertToMapMessage(currentMessage, publisher.getSession());
            publisher.sendMessage(mapMessage);
            LOG.info("Gesendet {}: ", mapMessage);
            
        } catch (JMSException jmse) {
            LOG.error("[*** JMSException ***]: {}", jmse.getMessage());
            this.monitorStatus.addCheckStatusInfo(System.currentTimeMillis(),
                                                  CheckStatus.ERROR, ErrorReason.JMS);
            closeJms();
            return;
        } catch (AmsMonitorException asme) {
            LOG.error("[*** AmsSystemMonitorException ***]: {}", asme.getMessage());
            this.monitorStatus.addCheckStatusInfo(System.currentTimeMillis(),
                                                  CheckStatus.ERROR, ErrorReason.JMS);
            closeJms();
            return;
        } 

        long waitUntil = System.currentTimeMillis() + waitTime;
        
        // Wait some time
        boolean found = false;
        do {
            while (!messageQueue.isEmpty()) {
                DeliveryWorkerAnswerMessage o = messageQueue.poll();
                if (o.isAnswerForMessage(currentMessage)) {
                    LOG.info("Success: It is the answer for the current message.");
                    found = true;
                } else if (monitorStatus.isAnswerForOlderMessage(o)) {
                    LOG.info("Success: The memory contains the initiator message.");
                    found = true;
                }
                if (found) {
                    monitorStatus.forgetAll();
                    String value = o.getValue();
                    currentStatus.setCheckStatus(CheckStatus.getCheckStatusByName(value));
                    currentStatus.setErrorReason(ErrorReason.DELIVERY_DEVICE);
                    currentStatus.setErrorText(o.getTextValue());
                    break;
                }
            }
            if (!found) {
                synchronized (this) {
                    try {
                        this.wait(1000L);
                    } catch (InterruptedException ie) {
                        LOG.warn("I have been interrupted.");
                    }
                }
            }
        } while ((System.currentTimeMillis() <= waitUntil) && !found);
        
        // Stop the asynchronous message receiving
        try {
            consumer.removeMessageListener();
        } catch (JMSException e) {
            LOG.warn("[*** JMSException ***]: Cannot remove message listener.");
        }
        
        if (currentStatus.getCheckStatus() == CheckStatus.UNDEFINED) {
            currentStatus.setCheckStatus(CheckStatus.ERROR);
            currentStatus.setErrorReason(ErrorReason.SMS_DELIVERY_WORKER);
            this.monitorStatus.addCheckStatusInfo(currentStatus);
        }
        
        closeJms();
    }
    
    @Override
    public synchronized void onMessage(Message message) {
        MapMessage mm = (MapMessage) message;
        DeliveryWorkerAnswerMessage dwam = messageConverter.convertToDeliveryWorkerAnswerMessage(mm);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}", dwam.toString());
        }
        if (dwam.isValid()) {
            messageQueue.add(dwam);
            LOG.info("Received message has been stored in message queue.");
        }
    }    
}
