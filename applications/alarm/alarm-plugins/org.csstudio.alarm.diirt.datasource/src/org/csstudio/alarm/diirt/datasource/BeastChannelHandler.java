/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import java.util.logging.Logger;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.datasource.MultiplexedChannelHandler;
import org.diirt.datasource.ValueCache;

import static org.diirt.vtype.ValueFactory.*;
/**
 * @author Kunal Shroff
 *
 */
public class BeastChannelHandler extends MultiplexedChannelHandler<BeastConnectionPayload, BeastMessagePayload> {

    private static final Logger log = Logger.getLogger(BeastChannelHandler.class.getName());

    private BeastDataSource beastDatasource;
    private MessageConsumer consumer;

    private String filter; 
    private String readType;
    private String writeType;
    
    public BeastChannelHandler(String channelName, BeastDataSource jmsDatasource) {
        super(channelName);
        this.beastDatasource = jmsDatasource;
    }

    public void setSelectors(String selector) {
        this.filter = selector;
    }

    public String getSelector() {
        return filter;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }

    public String getReadType() {
        return readType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    public String getWriteType() {
        return writeType;
    }

    @Override
    protected void connect() {
        try {
            Destination destination = beastDatasource.getSession().createTopic(getChannelName());
            // Create a MessageConsumer from the Session to the Topic or
            // Queue
            consumer = beastDatasource.getSession().createConsumer(destination);
            consumer.setMessageListener(new MessageListener() {

                @Override
                public void onMessage(Message message) {
                    log.info("message event: " + message.toString());
                    processMessage(new BeastMessagePayload(message, filter));
                }
            });
        } catch (JMSException e) {
            reportExceptionToAllReadersAndWriters(e);
            e.printStackTrace();
        }
        log.info("Prcoessing connection for " + getChannelName());
        processConnection(new BeastConnectionPayload(this));
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
            switch (getWriteType()) {
            case "VTable":
                String key = text.split(":")[0].trim();
                String value = text.split(":")[1].trim();
                MapMessage mapMessage = beastDatasource.getSession().createMapMessage();
                mapMessage.setString(key, value);
                if (filter != null) {
                    mapMessage.setString("NAME", filter);
                }
                // Tell the producer to send the message
                log.info("Sent table message: " + mapMessage.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(mapMessage);
                break;
            default:
                TextMessage textMessage = beastDatasource.getSession().createTextMessage(text);
                // Tell the producer to send the message
                log.info("Sent string message: " + textMessage.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(textMessage);
                break;
            }
           
            callback.channelWritten(null);
        } catch (JMSException e) {
            reportExceptionToAllReadersAndWriters(e);
            callback.channelWritten(e);
            e.printStackTrace();
        }
    }
    
    @Override
    protected boolean isWriteConnected(BeastConnectionPayload payload) {
        return true;
    }

    @Override
    protected BeastTypeAdapter findTypeAdapter(ValueCache<?> cache, BeastConnectionPayload connPayload) {
        return beastDatasource.getTypeSupport().find(cache, connPayload);
    }

    public BeastDataSource getBeastDatasource() {
        return beastDatasource;
    }
}
