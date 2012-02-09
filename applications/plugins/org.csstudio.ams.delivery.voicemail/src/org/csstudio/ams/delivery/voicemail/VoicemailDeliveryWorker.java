
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

import java.util.ArrayList;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
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
    private JmsRedundantReceiver amsReceiver; 
    
    private OutgoingVoicemailQueue messageQueue;
    
    private boolean running;

    /** This flag indicates if the thread should check its working state */
    private boolean checkState;

    /**
     * Constructor.
     */
    public VoicemailDeliveryWorker() {
        workerName = this.getClass().getSimpleName();
        messageQueue = new OutgoingVoicemailQueue();
        running = true;
        checkState = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        
        while(running) {
            synchronized (messageQueue) {
                try {
                    messageQueue.wait();
                    checkState = false;
                } catch (InterruptedException ie) {
                    LOG.error("I have been interrupted.");
                }
            }

            int sent = 0;
            while (messageQueue.hasContent()) {
                // Get all messages and remove them
                ArrayList<VoicemailAlarmMessage> outgoing = messageQueue.getCurrentContent();
                LOG.info("Number of messages to send: " + outgoing.size());
            }
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWorking() {
        checkState = true;
        synchronized (messageQueue) {
            messageQueue.notify();
        }
        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait(250);
            } catch (InterruptedException e) {
                // Ignore me
            }
        }
        return !checkState;
    }
}
