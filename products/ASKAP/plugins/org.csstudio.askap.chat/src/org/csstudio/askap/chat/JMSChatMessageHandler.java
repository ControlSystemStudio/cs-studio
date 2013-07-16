package org.csstudio.askap.chat;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.InvalidClientIDException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.RemoveInfo;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;

public class JMSChatMessageHandler implements ChatMessageHandler {

	private static Logger logger = Logger.getLogger(ChatMessageHandler.class.getName());

    private Connection connection;
    private MessageProducer sender;
    private Session senderSession;
	private String userName;

	// if the same person logs on from multiple CSS, only the first instance is
	// a durable subscriber. Since the message only needs to be delivered to the
	// user once.
	private boolean isDurableSubscriber = true;	
	private boolean isRetreiveHistoryMsg = true;
	
	private ChatListener chatMessageListener;
	
	private Map<ConsumerId, String> subscriptionNameMap = new Hashtable<ConsumerId, String>();
	

    private class ChatMessageListener implements MessageListener {
    	
		@Override
		public void onMessage(Message msg) {
			
			try {
				MapMessage mapMessage = (MapMessage) msg;
				String from = mapMessage.getString("FROM");
				String text = mapMessage.getString("MESSAGE");
				long timeStamp = mapMessage.getJMSTimestamp();
								
				chatMessageListener.receive(from, text, timeStamp, userName.equals(from));
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not process a chat message received", e);
			}
		} 	
    }
    
    private class AdvisoryMessageListener implements MessageListener {

		@Override
		public void onMessage(Message msg) {
			ActiveMQMessage aMsg =  (ActiveMQMessage)msg;
			DataStructure data = aMsg.getDataStructure();
			logger.log(Level.INFO, "Got advisory message- " + data.toString());
			
			if (data instanceof RemoveInfo) {
				RemoveInfo removeData = (RemoveInfo) data;
				String subscriber = subscriptionNameMap.get(removeData.getObjectId());
//				logger.log(Level.INFO, "Remove consumer " + subscriber);
				
				if (subscriber!=null) {
					if (userName.equals(subscriber)) {
						logger.log(Level.INFO, "Taking over chat durable subscriber");
						
						// since this is inside receive thread, need to stop and start connection
						// in another thread
						
						Thread chatThread = new Thread(new Runnable() {						
							@Override
							public void run() {
								try {
									stopChat();
									isRetreiveHistoryMsg = false;
									startChat();
								} catch (Exception e) {
									logger.log(Level.WARNING, "Having problem taking over as durable subscriber.", e);
								}
							}
						});
						
						chatThread.start();
					} else {
						chatMessageListener.removeParticiparnt(subscriber);
					}
				}
				
			} else if (data instanceof ConsumerInfo) {
				ConsumerInfo consumerData = (ConsumerInfo) data;
				String subscriber = consumerData.getSubscriptionName();
//				logger.log(Level.INFO, "Add consumer " + subscriber);
				
				if (subscriber!=null) {
					subscriptionNameMap.put(consumerData.getConsumerId(), subscriber);					
					// skip self
					if (userName.equals(subscriber))
						return;
					
					chatMessageListener.addParticipant(subscriber);
				}
			}
 			
		}
    	
    }

    
    public JMSChatMessageHandler(String userName, ChatListener listener) {
    	this.userName = userName;
    	this.chatMessageListener = listener;
    }
    
    
	@Override
    public void startChat() throws Exception {
		logger.log(Level.INFO, "Starting chat session");

		subscriptionNameMap.clear();
		
//			String url = "failover:(tcp://mtos1.atnf.csiro.au:61616)";
		String url = Preferences.getServerURL();
        connection = JMSConnectionFactory.connect(url);
        
        try {
        	connection.setClientID(userName);
        	isDurableSubscriber = true;
        } catch (InvalidClientIDException e) {
        	// this client is already connected from another CSS, so this user will be non-durable
        	connection.setClientID(null);
        	isDurableSubscriber = false;
        }
        
        Session session = connection.createSession(false,
                                           Session.AUTO_ACKNOWLEDGE);
        
        String topicName = Preferences.getTopicName();
        
        if (isRetreiveHistoryMsg)
        	topicName = topicName + "?consumer.retroactive=true";
        
		logger.log(Level.INFO, "Create consumer for " + topicName);
        Topic chatTopic = session.createTopic(topicName); 
        
        MessageConsumer chatSubscriber = null;
        
        if (isDurableSubscriber) {
        	chatSubscriber = session.createDurableSubscriber(chatTopic, userName);
			logger.log(Level.INFO, "Create durable subscriber as " + userName);
        } else {
        	chatSubscriber = session.createConsumer(chatTopic);
			logger.log(Level.INFO, "Create subscriber");
        }
        
        chatSubscriber.setMessageListener(new ChatMessageListener());
                
        chatTopic = session.createTopic(Preferences.getTopicName()); 
		senderSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		sender = senderSession.createProducer(chatTopic);

        Session advisorySession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic advisoryTopic = advisorySession.createTopic("ActiveMQ.Advisory.Consumer.Topic." + Preferences.getTopicName());
        MessageConsumer consumer = advisorySession.createConsumer(advisoryTopic);
        consumer.setMessageListener(new AdvisoryMessageListener());
		
        isRetreiveHistoryMsg = true;
		connection.start();
	}


	@Override
	public void stopChat() throws Exception {
		logger.log(Level.INFO, "Stop chat session");
		connection.close();
	}



	@Override
	public void sendChatMessage(String message) throws Exception {
		MapMessage msg = senderSession.createMapMessage();
		msg.setString("FROM", userName);
		msg.setString("MESSAGE", message);
		msg.setJMSExpiration(Preferences.getTimeToLive());
		
		sender.send(msg);
	}


	@Override
	public Collection<String> getParticipants() throws Exception {
		return subscriptionNameMap.values();
	}
}
