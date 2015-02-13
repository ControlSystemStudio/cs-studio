package org.csstudio.askap.chat;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;

public class TestPublisher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
//			String url = "failover:(tcp://mtos1.atnf.csiro.au:61616)";
			
//			String url = "failover:(tcp://aktos02c.atnf.csiro.au:61616)";
			String url = "failover:(tcp://localhost:61616)";
			Connection connection = JMSConnectionFactory.connect(url);

			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Topic chatTopic = session.createTopic("ASKAP_Chat");
			
			MessageProducer publisher = session.createProducer(chatTopic);
			publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			
			MapMessage map = session.createMapMessage();
			map.setString("FROM", "Robert");
			map.setString("MESSAGE", "Hello Xinyu again!");
			publisher.send(map);
			
			System.out.println(map.toString());			
			
			map.setString("FROM", "Tony");
			map.setString("MESSAGE", "Hello world again!");
			publisher.send(map);

			System.out.println(map.toString());			

			map.setString("FROM", "JC");
			map.setString(
					"MESSAGE",
					"Hello TOS again! Let me try and send a really really really long message and see what happens. I wonder if this message is going to be long enough. Maybe I'll just make it just a bit longer to make sure");
			publisher.send(map);

			System.out.println(map.toString());			

			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
