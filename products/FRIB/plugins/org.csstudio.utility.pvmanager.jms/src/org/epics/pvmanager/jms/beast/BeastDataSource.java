/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.beast;

import org.epics.pvmanager.jms.beast.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private final String topicName;
    private final String url;

    /**
     *
     */
    public BeastDataSource(String topicName, String url) {
        super(true);
        this.topicName = topicName;
        this.url = url;
        this.typeSupport = new BeastTypeSupport(new BeastVTypeAdapterSet());
    }
    
    public BeastDataSource(BeastDataSource datasource) {
    	super(true);
        //final CountDownLatch startCommenced = new CountDownLatch(1);
        //final CountDownLatch startDone = new CountDownLatch(1);
        
    	this.topicName = datasource.topicName;
    	this.url = datasource.url;
        this.typeSupport = datasource.typeSupport;
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
            	//startCommenced.countDown();
                try {

                	ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
         			connection = factory.createConnection();
                    connection.start();
                   // startDone.countDown();
                } catch (Exception ex) {
                	Logger.getLogger(BeastTypeSupport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }});
       // try {
			//startCommenced.await(5, TimeUnit.SECONDS);
			//startDone.await(10, TimeUnit.SECONDS);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
    	
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
