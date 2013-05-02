/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.vtype.DataTypeSupport;

/**
 *
 * @author msekoranja
 */
public class JMSDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }
    private Connection connection;
    private final JMSTypeSupport typeSupport;

    /**
     *
     */
    public JMSDataSource(String topicName) throws JMSException {
        super(true);
        typeSupport = new JMSTypeSupport(new JMSJavaTypeAdapterSet());
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("user", "password", "alarm.hlc.nscl.msu.edu");
        connection = factory.createConnection();
        connection.start();
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(JMSDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected ChannelHandler createChannel(String topicName) {
        return new JMSChannelHandler(topicName, this);
    }

    public Connection getConnection() {
        return connection;
    }

    public JMSTypeSupport getTypeSupport() {
        return typeSupport;
    }
}
