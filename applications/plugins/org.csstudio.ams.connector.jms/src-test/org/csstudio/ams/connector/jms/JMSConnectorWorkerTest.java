package org.csstudio.ams.connector.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JMSConnectorWorkerTest {
    
    private Session _jmsSession;
    private Connection _jmsConnection;

    @Before
    public void createTestConnection() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        _jmsConnection = connectionFactory.createConnection();
        _jmsConnection.start();
        _jmsSession = _jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
    
    @After
    public void closeTestConnection() throws Exception {
        _jmsConnection.close();
    }
    
    @Test
    public void testConnectorWorker() throws Exception {
        MapMessage testMessage = _jmsSession.createMapMessage();
        testMessage.setString("AMS-RECEIVER-ADDR", "TEST_TARGET");
        testMessage.setString("AMS-EXTENDED-MESSAGE", "true");
        testMessage.setString("NAME", "Test");
        testMessage.setString("FOO", "bar");
        MessageConsumer consumer = _jmsSession.createConsumer(_jmsSession.createTopic("TEST_TARGET"));
        
        JMSConnectorWorker worker = new JMSConnectorWorker(_jmsSession);
        worker.onMessage(testMessage);
        
        MapMessage result = (MapMessage) consumer.receive(1000);
        assertEquals("Test", result.getString("NAME"));
        assertEquals("bar", result.getString("FOO"));
        assertFalse(result.itemExists("AMS-RECEIVER-ADDR"));
        assertFalse(result.itemExists("AMS-EXTENDED-MESSAGE"));
    }
    
    @Test
    public void testNeverCrash() throws Exception {
        MapMessage testMessage = _jmsSession.createMapMessage();
        testMessage.setString("FOO", "bar");
        
        JMSConnectorWorker worker = new JMSConnectorWorker(_jmsSession);
        // Message which does not contain AMS-RECEIVER-ADDR
        worker.onMessage(testMessage);
        
        // null message
        worker.onMessage(null);
        
        // not a MapMessage
        worker.onMessage(_jmsSession.createTextMessage());
        
        // session closed
        testMessage = _jmsSession.createMapMessage();
        testMessage.setString("AMS-RECEIVER-ADDR", "TEST_TARGET");
        testMessage.setString("AMS-EXTENDED-MESSAGE", "true");
        testMessage.setString("FOO", "bar");
        _jmsSession.close();
        worker.onMessage(testMessage);
    }
}
