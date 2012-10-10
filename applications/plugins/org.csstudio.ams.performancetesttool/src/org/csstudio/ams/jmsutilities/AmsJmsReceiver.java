package org.csstudio.ams.jmsutilities;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.beust.jcommander.JCommander;

public class AmsJmsReceiver {
    
    class AmsMessageLogger implements MessageListener {

    	private final String loggerName;

		public AmsMessageLogger(String loggerName) {
			this.loggerName = loggerName;
		}
    	
        @Override
        public void onMessage(Message message) {
        	if(message instanceof MapMessage) {
        		Enumeration<?> mapNames;
				try {
					MapMessage mapMessage = ((MapMessage) message);
					mapNames = mapMessage.getMapNames();
					if(mapNames.hasMoreElements()) {
						System.out.println(loggerName + ": ");
					}
					while(mapNames.hasMoreElements()) {
						Object nextElement = mapNames.nextElement();
						System.out.println(nextElement + " = " + mapMessage.getString((String) nextElement));
					}
					
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	System.out.println(loggerName + ": " + message.toString());
        	System.out.println();
        }
        
    }
    
    private final AmsJmsCommandLineArgs options;
    private List<Connection> connections;

    public AmsJmsReceiver(AmsJmsCommandLineArgs options) {
        this.options = options;
        connections = new ArrayList<Connection>();
    }

    public void run() throws JMSException {
        connectJMS();
        startListening();
    }

    private void connectJMS() throws JMSException {
        for (String uri : options.uris) {
            ConnectionFactory cf = new ActiveMQConnectionFactory(uri);
            Connection conn = cf.createConnection();
            connections.add(conn);
            conn.start();
        }
    }
    
    private void startListening() throws JMSException {
        try {
            for (Connection c : connections) {
                final Session session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
                for (String topicName : options.topics) {
                    Topic topic = session.createTopic(topicName);
                    MessageConsumer consumer = session.createConsumer(topic);
                    AmsMessageLogger topicLogger = new AmsMessageLogger(topic.getTopicName()); 
                    consumer.setMessageListener(topicLogger);
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException("JMS error during listener registration", e);
        }
    }
    
    public static void main(String[] args) {
        AmsJmsCommandLineArgs arguments = new AmsJmsCommandLineArgs();
        new JCommander(arguments, args);
        try {
            new AmsJmsReceiver(arguments).run();
        } catch (Exception e) {
            System.err.println("An error occured, the test was aborted");
            System.err.println(e.getMessage());
        }
    }
}
