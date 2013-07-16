package org.csstudio.askap.chat;

import javax.jms.Connection;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;

public class TestSubscriber implements MessageListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
//		String url = "failover:(tcp://mtos1.atnf.csiro.au:61616)";
			String url = "failover:(tcp://localhost:61616)";
	        Connection connection = JMSConnectionFactory.connect(url);
	        
	        Session session = connection.createSession(false,
	                                           Session.AUTO_ACKNOWLEDGE);
	        
	        Topic chatTopic = session.createTopic("ASKAP_Chat?consumer.retroactive=true");
//	        Topic chatTopic = session.createTopic("ASKAP_Chat");
	        
	        MessageConsumer chatSubscriber = session.createConsumer(chatTopic);
	        
	        chatSubscriber.setMessageListener(new TestSubscriber());
	        
	        connection.start();
        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message msg) {
		
		try {
			System.out.println(msg.toString());		
			MapMessage map = (MapMessage) msg;
			
			System.out.println("From: " + map.getString("FROM"));		
			
			System.out.println("Message: " + map.getString("MESSAGE"));	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
