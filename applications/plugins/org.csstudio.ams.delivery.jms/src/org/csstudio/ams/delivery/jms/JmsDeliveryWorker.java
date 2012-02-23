
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

import org.csstudio.ams.delivery.AbstractDeliveryWorker;
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
        jmsDevice = new JmsDevice();
        running = true;
        workerCheckFlag = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
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
