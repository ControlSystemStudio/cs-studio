package org.csstudio.ams.connector.jms;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;

/**
 * Receives messages and forwards them to their destination topic.
 */
public class JMSConnectorWorker implements MessageListener {

    private final Session _session;
    private final MessageProducer _producer;

    /**
     * Creates a new worker.
     *
     * @param session the session that will be used to send messages.
     * @throws JMSException if no producer could be created for the given JMS session.
     */
    public JMSConnectorWorker(final Session session) throws JMSException {
        if (session == null) {
            throw new IllegalArgumentException("session was null");
        }

        _session = session;
        _producer = _session.createProducer(null);
    }

    @Override
    public void onMessage(final Message message) {
        if (!(message instanceof MapMessage)) {
            Log.log(Log.WARN, "The message is NOT a MapMessage object.");
            return; // ignore the message
        }

        try {
            final MapMessage receivedMessage = (MapMessage) message;
            final Topic target = targetTopicFor(receivedMessage);
            final MapMessage outgoingMessage = _session.createMapMessage();
            copyMessageEntries(receivedMessage, outgoingMessage);
            Log.log(Log.INFO, "Recieved a message, now trying to send...");
            _producer.send(target, outgoingMessage);
            Log.log(Log.INFO, "Message succesfully sent to topic \"" + target.getTopicName() + "\"");
        } catch (final Exception e) {
            Log.log(Log.ERROR, "Failed to forward a message", e);
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
