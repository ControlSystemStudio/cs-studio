package org.csstudio.askap.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.platform.utility.jms.JMSConnectionFactory;

public class JMSChatMessageHandler implements ChatMessageHandler {

	private static Logger logger = Logger.getLogger(ChatMessageHandler.class.getName());

    private Connection connection;
    private MessageProducer sender;
    private MessageProducer heartBeatProducer;
    private Session senderSession;
	private String userName;
	
	private ChatListener chatMessageListener;
	
	private Map<String, Date> subscriptionNameMap = new Hashtable<String, Date>();
	
	private HeartBeatThread heartBeatThread;

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
    
    private class HeartBeatMessageListener implements MessageListener {

		@Override
		public void onMessage(Message msg) {
			try {				
				MapMessage heartBeat = (MapMessage) msg;
	
				String from = heartBeat.getString("FROM");
				long timestamp = heartBeat.getJMSTimestamp();
				
				
				if (subscriptionNameMap.get(from) == null) {
					if (!userName.equals(from))
						chatMessageListener.addParticipant(from);
				}
				
				subscriptionNameMap.put(from, new Date(timestamp));				
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not process heartbeat msg: ", e);
			}			
		}
    	
    }

    // start the thread to send heartbeat and check if all the users have sent heart beat
    private class HeartBeatThread implements Runnable {
    	
    	boolean keepRunning = true;
		
		@Override
		public void run() {
			
			while (keepRunning) {
				
				try {
					// first send a heart beat message
					MapMessage heartBeatMsg = senderSession.createMapMessage();
					heartBeatMsg.setJMSTimestamp(System.currentTimeMillis());
					heartBeatMsg.setString("FROM", userName);
					
					heartBeatProducer.send(heartBeatMsg);
				} catch (Exception e) {
					logger.log(Level.WARNING, "Could not send heartbeat message", e);
				}
				
				// then check heart beats of the other users
				long timeLimit = System.currentTimeMillis() - Preferences.getHeartBeatMinPeriod();
				
				Set<String> userList = subscriptionNameMap.keySet();
				List<String> removeList = new ArrayList<String>();
				
				for (String user : userList) {
					long timestamp = subscriptionNameMap.get(user).getTime();
					if (timestamp < timeLimit) {
						removeList.add(user);
					}							
				}
				
				for (String user : removeList) {
					subscriptionNameMap.remove(user);
					chatMessageListener.removeParticiparnt(user);
				}
				
				try {
					Thread.sleep(Preferences.getHeartBeatPeriod());
				} catch (InterruptedException e) {
					// no need to do anything
				}
			}
		}
		
		public void stopThread() {
			keepRunning = false;
		}
	};

    
    
    public JMSChatMessageHandler(String userName, ChatListener listener) {
    	this.userName = userName;
    	this.chatMessageListener = listener;
    }
    
    
	@Override
    public void startChat() throws Exception {
		logger.log(Level.INFO, "Starting chat session");

		subscriptionNameMap.clear();
		
		String url = Preferences.getServerURL();
        connection = JMSConnectionFactory.connect(url);
        
        Session session = connection.createSession(false,
                                           Session.AUTO_ACKNOWLEDGE);
        
        String topicName = Preferences.getMessageTopicName() + "?consumer.retroactive=true";
        
		logger.log(Level.INFO, "Create consumer for " + topicName);
        Topic chatTopic = session.createTopic(topicName); 
        
        MessageConsumer chatSubscriber = session.createConsumer(chatTopic);        
        chatSubscriber.setMessageListener(new ChatMessageListener());
                
        chatTopic = session.createTopic(Preferences.getMessageTopicName()); 
		senderSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		sender = senderSession.createProducer(chatTopic);

		
        Session heartBeatSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic heartBeatTopic = heartBeatSession.createTopic(Preferences.getHeasrtBeatTopicName());
        MessageConsumer consumer = heartBeatSession.createConsumer(heartBeatTopic);
        consumer.setMessageListener(new HeartBeatMessageListener());
        
        heartBeatProducer = senderSession.createProducer(heartBeatTopic);
        
        
        heartBeatThread = new HeartBeatThread();
		new Thread(heartBeatThread).start();

 		connection.start();
	}


	@Override
	public void stopChat() throws Exception {
		logger.log(Level.INFO, "Stop chat session");
		
		heartBeatThread.stopThread();
		
		// close connection
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
		return subscriptionNameMap.keySet();
	}


	@Override
	public void changeUserName(String newName) {
		this.userName = newName;
	}
}
