package org.csstudio.askap.chat;

import javax.jms.Connection;
import javax.jms.Session;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;

public class TestRemoveDurableSubscriber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
//		String url = "failover:(tcp://mtos1.atnf.csiro.au:61616)";
			String url = "failover:(tcp://localhost:61616)";
	        Connection connection = JMSConnectionFactory.connect(url);
	        
	        String userName = "wu049";
	        if (args!=null && args.length>0)
	        	userName = args[0];

	        connection.setClientID(userName);
	        
	        System.out.println("Remove Subscriber - " + userName);
	        
	        Session session = connection.createSession(false,
	                                           Session.AUTO_ACKNOWLEDGE);
	        
	        session.unsubscribe(userName);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        System.out.println("Finished!");
        System.exit(0);        
	}

}
