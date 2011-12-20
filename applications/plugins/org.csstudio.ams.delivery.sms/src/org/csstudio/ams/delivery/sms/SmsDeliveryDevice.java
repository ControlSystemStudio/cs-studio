
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

import java.util.Collection;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.csstudio.ams.delivery.BaseAlarmMessage;
import org.csstudio.ams.delivery.device.DeviceException;
import org.csstudio.ams.delivery.device.IDeliveryDevice;
import org.csstudio.ams.delivery.jms.JmsAsyncConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public class SmsDeliveryDevice implements IDeliveryDevice, MessageListener {

    /** The static class logger */
    private static final Logger LOG = LoggerFactory.getLogger(SmsDeliveryDevice.class);
    
    /** The consumer is necessary to receive the device test messages */
    private JmsAsyncConsumer amsConsumer;
    
    public SmsDeliveryDevice(JmsAsyncConsumer consumer) {
        amsConsumer = consumer;
        amsConsumer.addMessageListener("amsSubscriberSmsModemtest", this);
    }
    
    @Override
    public int sendMessages(Collection<BaseAlarmMessage> msgList) throws DeviceException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean deleteMessage(BaseAlarmMessage message) throws DeviceException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean sendMessage(BaseAlarmMessage message) throws DeviceException {
        return true;
    }

    @Override
    public BaseAlarmMessage readMessage() {
        return null;
    }

    @Override
    public void readMessages(Collection<BaseAlarmMessage> msgList) throws DeviceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopDevice() {
        if (amsConsumer != null) {
            amsConsumer.closeSubscriber("amsSubscriberSmsModemtest");
        }
    }

    @Override
    public void onMessage(Message msg) {
        LOG.info("Message received: {}", msg);
        acknowledge(msg);
    }
    
    private void acknowledge(Message message) {
        try {
            message.acknowledge();
        } catch (JMSException jmse) {
            LOG.warn("Cannot acknowledge message: {}", message);
        }
    }
}
