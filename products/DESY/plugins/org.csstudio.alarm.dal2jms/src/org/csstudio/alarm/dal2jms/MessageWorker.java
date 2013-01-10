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
package org.csstudio.alarm.dal2jms;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IRemoteAcknowledgeService;
import org.csstudio.servicelocator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Queue to decouple listening to alarm service and forwarding messages to the jms server.
 * 
 * @author jpenning
 * @since 27.01.2012
 */
public class MessageWorker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MessageWorker.class);
    
    private BlockingQueue<IAlarmMessage> _queue = new LinkedBlockingQueue<IAlarmMessage>();
    private final JmsMessageService _jmsMessageService;

    
    public MessageWorker(@Nonnull final JmsMessageService jmsMessageService) {
        _jmsMessageService = jmsMessageService;
        Thread thread = new Thread(this, "dal2jms message worker");
        thread.setDaemon(true);
        thread.start();
    }
    
    public void enqueue(@Nonnull final IAlarmMessage message) {
        LOG.trace("Message '{}' enqueued", message);
        boolean result = _queue.offer(message);
        if (!result) {
            LOG.trace("Offering message to queue failed for '{}'", message);
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                IAlarmMessage alarmMessage = _queue.take();
                LOG.trace("Message taken from queue. Current size " + _queue.size());
                _jmsMessageService.sendAlarmMessage(alarmMessage);
                sendToAcknowledgeServer(alarmMessage);
            } catch (InterruptedException e) {
                LOG.debug("Error while forwarding message. Queue-Take was interrupted.", e);
            } catch (Exception e) {
                LOG.error("Error while forwarding message. Exception occurred.", e);
            }
        }
    }
    
    private void sendToAcknowledgeServer(@Nonnull final IAlarmMessage alarmMessage) {
        String pvName = "(unknown)";
        try {
            pvName = alarmMessage.getString(AlarmMessageKey.NAME);
            LOG.trace("Announce Alarm for {} to Ack-Server", pvName);
            IRemoteAcknowledgeService ackService = ServiceLocator.getService(IRemoteAcknowledgeService.class);
            ackService.announceAlarm(pvName);
        } catch (RemoteException e) {
            LOG.error("Error while announcing alarm to " + pvName + ". RemoteException occurred.",
                      e);
        } catch (Exception e) {
            LOG.error("Error while announcing alarm to " + pvName + " . Exception occurred.", e);
        }
    }
    
}
