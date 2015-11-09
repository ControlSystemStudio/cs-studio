/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.logging.Logger;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.datasource.MultiplexedChannelHandler;

import static org.diirt.vtype.ValueFactory.*;
/**
 * @author Kunal Shroff
 *
 */
public class BeastChannelHandler extends MultiplexedChannelHandler<Object, Object> {

    private static final Logger log = Logger.getLogger(BeastChannelHandler.class.getName());

    private BeastDatasource beastDatasource;
    private MessageConsumer consumer;

    private String selector; 
    private Object readType;
    private Object writeType;
    
    public BeastChannelHandler(String channelName, BeastDatasource jmsDatasource) {
        super(channelName);
        this.beastDatasource = jmsDatasource;
    }

    public void setSelectors(String selector) {
        this.selector = selector;
    }

    public void setReadType(Object readType) {
        this.readType = readType;
    }

    public void setWriteType(Object writeType) {
        this.writeType = writeType;
    }

    @Override
    protected void connect() {
        try {
            Destination destination = beastDatasource.getSession().createTopic(getChannelName());
            // Create a MessageConsumer from the Session to the Topic or
            // Queue
            if (selector != null && !selector.isEmpty()) {
                consumer = beastDatasource.getSession().createConsumer(destination, selector);
            } else {
                consumer = beastDatasource.getSession().createConsumer(destination);
            }
            consumer.setMessageListener(new MessageListener() {

                @Override
                public void onMessage(Message message) {
                    log.info("message event: " + message.toString());
                    Object newValue;
                    try {
                        log.fine("creating new values");
                        newValue = newVString(message.toString(), alarmNone(), timeNow());
                        log.fine("new Value: " + newValue);
                        processMessage(newValue);
                    } catch (Exception e) {
                        reportExceptionToAllReadersAndWriters(e);
                    }
                    // processMessage(new JMSMessagePayload(message));
                }
            });
        } catch (JMSException e) {
            reportExceptionToAllReadersAndWriters(e);
            e.printStackTrace();
        }
        processConnection(new Object());
    }

    @Override
    protected void disconnect() {
        try {
            log.info("channel close");
            consumer.close();
            processConnection(null);
        } catch (JMSException e) {
            reportExceptionToAllReadersAndWriters(e);
            // TODO cleanup
            e.printStackTrace();
        }
    }

    @Override
    public void write(Object newValue, ChannelWriteCallback callback) {
        try {
            Destination destination = beastDatasource.getSession().createTopic(getChannelName());
            // Create a MessageProducer from the Session to the Topic or
            // Queue
            MessageProducer producer = beastDatasource.getSession().createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // Create a messages
            String text = newValue.toString();
            TextMessage message = beastDatasource.getSession().createTextMessage(text);
            // Tell the producer to send the message
            log.info("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);
            callback.channelWritten(null);
        } catch (JMSException e) {
            reportExceptionToAllReadersAndWriters(e);
            callback.channelWritten(e);
            e.printStackTrace();
        }
    }
    
    @Override
    protected boolean isWriteConnected(Object payload) {
        return true;
    }


}
