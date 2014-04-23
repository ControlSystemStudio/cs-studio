/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.BytesMessage;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.transport.TransportListener;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ValueCache;

/**
 *
 * @author msekoranja
 */
public class JMSChannelHandler extends MultiplexedChannelHandler<JMSChannelHandler, Message> implements MessageListener {

    private final String topicName;
    private MessageConsumer consumer;
    private MessageProducer producer;
    private volatile Session session;
    private JMSDataSource jmsDataSource;

    /**
     *
     * @param chanelName
     */
    public JMSChannelHandler(String topicName, JMSDataSource jmsDataSource) {
        super(topicName);
        this.jmsDataSource = jmsDataSource;
        this.topicName = topicName;
    }

    @Override
    public void connect() {
        try {
            final ActiveMQConnection amq_connection = (ActiveMQConnection) jmsDataSource.getConnection();
            amq_connection.addTransportListener(transportListener);
            amq_connection.setExceptionListener(exceptionListener);
            session = jmsDataSource.getConnection().createSession(/* transacted */false, Session.AUTO_ACKNOWLEDGE);
            this.producer = createProducer(topicName);
            this.consumer = createConsumer(topicName);
            this.consumer.setMessageListener(this);

        } catch (JMSException ex) {
            Logger.getLogger(JMSChannelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @Override
    public boolean isConnected(JMSChannelHandler jmsChannelHandler) {
        final ActiveMQConnection amq_connection = ((ActiveMQSession) jmsChannelHandler.session).getConnection();
        return amq_connection.getTransport().isConnected();

    }

    @Override
    public void disconnect() {
        try {
            session.close();
        } catch (JMSException ex) {
            Logger.getLogger(JMSChannelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected JMSTypeAdapter findTypeAdapter(ValueCache<?> cache, JMSChannelHandler jmsChannelHandler) {
        return jmsDataSource.getTypeSupport().find(cache, jmsChannelHandler);
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            if (newValue instanceof String) {
                TextMessage message = session.createTextMessage();
                message.setText((String) newValue);
                producer.send(message);
            } else if (newValue instanceof byte[]) {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes((byte[]) newValue);
                producer.send(message);
            } else if (newValue instanceof Integer) {
                StreamMessage message = session.createStreamMessage();
                message.writeInt(((Integer) newValue).intValue());
                producer.send(message);
            } else if (newValue instanceof Float) {
                StreamMessage message = session.createStreamMessage();
                message.writeFloat(((Float) newValue).floatValue());
                producer.send(message);
            } else if (newValue instanceof Double) {
                StreamMessage message = session.createStreamMessage();
                message.writeDouble(((Double) newValue).doubleValue());
                producer.send(message);
            } else if (newValue instanceof Long) {
                StreamMessage message = session.createStreamMessage();
                message.writeLong(((Long) newValue).longValue());
                producer.send(message);
            } else if (newValue instanceof Byte) {
                StreamMessage message = session.createStreamMessage();
                message.writeByte(((Byte) newValue).byteValue());
                producer.send(message);
            } else if (newValue instanceof Character) {
                StreamMessage message = session.createStreamMessage();
                message.writeChar(((Character) newValue).charValue());
                producer.send(message);
            } else if (newValue instanceof Short) {
                StreamMessage message = session.createStreamMessage();
                message.writeShort(((Short) newValue).shortValue());
                producer.send(message);
            } else if (newValue instanceof Map) {
                MapMessage message = session.createMapMessage();
                for (Iterator it = ((Map) newValue).entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (!(entry.getKey() instanceof String)) {
                        throw new RuntimeException(
                                "Cannot convert non-String key of type ["
                                + (entry.getKey() != null ? entry.getKey().getClass().getName() : null)
                                + "] to MapMessage entry");
                    }
                    message.setObject((String) entry.getKey(), entry.getValue());
                }
                producer.send(message);
            } else if (newValue instanceof Serializable) {
                ObjectMessage message = session.createObjectMessage();
                message.setObject(((Serializable) newValue));
                producer.send(message);
            } else {
                throw new RuntimeException("Unsupported type for JMS: " + newValue.getClass());
            }
        } catch (JMSException ex) {
            Logger.getLogger(JMSChannelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message msg) {
        processMessage(msg);
    }
    private final TransportListener transportListener = new TransportListener() {
        @Override
        public void onCommand(Object o) {
        }

        @Override
        public void onException(IOException ioe) {
        }

        @Override
        public void transportInterupted() {
        }

        @Override
        public void transportResumed() {
        }
    };
    ;

    private final ExceptionListener exceptionListener = new ExceptionListener() {
        @Override
        public void onException(JMSException jmse) {
            Logger.getLogger(JMSChannelHandler.class.getName()).log(Level.SEVERE, null, jmse);
        }
    };

    ;
    /**
     * Create a producer. Derived class can use this to create one or more
     * producers, sending MapMessages to them in the communicator thread.
     *
     * @param topic_name Name of topic for the new producer
     * @return MessageProducer
     * @throws JMSException on error
     */
    protected MessageProducer createProducer(final String topic_name) throws JMSException {
        final Topic topic = session.createTopic(topic_name);
        final MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return producer;
    }

    /**
     * Create a consumer.
     *
     * @param topic_name Name of topic for the new consumer
     * @return MessageProducer
     * @throws JMSException on error
     */
    protected MessageConsumer createConsumer(final String topic_name) throws JMSException {
        final Topic topic = session.createTopic(topic_name);
        final MessageConsumer consumer = session.createConsumer(topic);
        return consumer;
    }

    public JMSDataSource getJmsDataSource() {
        return jmsDataSource;
    }
}
