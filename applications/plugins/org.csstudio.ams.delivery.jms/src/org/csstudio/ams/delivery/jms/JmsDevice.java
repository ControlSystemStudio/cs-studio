
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

import java.util.Collection;
import java.util.Enumeration;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.delivery.device.IDeliveryDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 23.02.2012
 */
public class JmsDevice implements IDeliveryDevice<JmsAlarmMessage>, MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JmsDevice.class);
    
    private final Session _session;
    private MessageProducer _producer;
    private JmsWorkerStatus workerStatus;
    
    public JmsDevice(final Session session, JmsWorkerStatus status) throws JMSException {
        if (session == null) {
            throw new IllegalArgumentException("Session was null");
        }
        _session = session;
        _producer = _session.createProducer(null);
        workerStatus = status;
    }
    
    @Override
    public void onMessage(final Message message) {
        if (!(message instanceof MapMessage)) {
            LOG.warn("The message is NOT a MapMessage object.");
            return; // ignore the message
        }

        try {
            final MapMessage receivedMessage = (MapMessage) message;
            final Topic target = targetTopicFor(receivedMessage);
            final MapMessage outgoingMessage = _session.createMapMessage();
            copyMessageEntries(receivedMessage, outgoingMessage);
            workerStatus.setJmsTimestamp(System.currentTimeMillis());
            LOG.info("Recieved a message, now trying to send...");
            _producer.send(target, outgoingMessage);
            LOG.info("Message succesfully sent to topic \"{}\"", target.getTopicName());
        } catch (final Exception e) {
            LOG.error("Failed to forward a message: {}", e.getMessage());
        }
    }

    @Override
    public boolean sendMessage(JmsAlarmMessage message) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public int sendMessages(Collection<JmsAlarmMessage> msgList) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void stopDevice() {
        if (_producer != null) {
            try {
                _producer.close();
            } catch (JMSException e) {
                _producer = null;
            }
        }
    }
    
    /**
     * Returns the target topic to which the given message should be forwarded.
     *
     * @param message the message.
     */
    private Topic targetTopicFor(final MapMessage message) throws JMSException {
        final String topicName = message.getString(AmsConstants.MSGPROP_RECEIVERADDR);
        if (topicName != null) {
            return _session.createTopic(topicName);
        }

        throw new RuntimeException("Received a message which did not contain "
                    + AmsConstants.MSGPROP_RECEIVERADDR);
    }

    /**
     * Copies the map entries from the source to the destination message. AMS-specific entries are not copied.
     */
    private void copyMessageEntries(final MapMessage source, final MapMessage destination) throws JMSException {
        @SuppressWarnings("unchecked")
        final
        Enumeration<String> mapNames = source.getMapNames();

        while (mapNames.hasMoreElements()) {
            final String key = mapNames.nextElement();
            if (!key.startsWith(AmsConstants.AMS_PREFIX)) {
                destination.setString(key, source.getString(key));
            }
        }
    }
}
