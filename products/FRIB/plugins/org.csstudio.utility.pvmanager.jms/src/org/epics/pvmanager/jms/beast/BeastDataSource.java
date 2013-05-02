/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.beast;

import org.epics.pvmanager.jms.beast.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.vtype.DataTypeSupport;

/**
 *
 * @author msekoranja
 */
public class BeastDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }
    private Connection connection;
    private final BeastTypeSupport typeSupport;
    private String topicName;
    private String url;

    /**
     *
     */
    public BeastDataSource(String topicName, String url) throws JMSException, Exception {
        super(true);
        this.topicName = topicName;
        this.url = url;
        typeSupport = new BeastTypeSupport(new BeastVTypeAdapterSet());
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        connection = factory.createConnection();
        connection.start();
    }
    
    public BeastDataSource(BeastDataSource datasource) throws JMSException, Exception {
    	super(true);
    	this.topicName = datasource.topicName;
    	this.url = datasource.url;
        typeSupport = new BeastTypeSupport(new BeastVTypeAdapterSet());
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        connection = factory.createConnection();
        connection.start();
    	
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(BeastTypeSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected ChannelHandler createChannel(String channel) {
        return new BeastChannelHandler(topicName, channel, this);
    }

    public Connection getConnection() {
        return connection;
    }

    public BeastTypeSupport getTypeSupport() {
        return typeSupport;
    }
}
