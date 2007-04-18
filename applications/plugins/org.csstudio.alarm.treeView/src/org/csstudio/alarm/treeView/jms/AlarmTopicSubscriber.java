package org.csstudio.alarm.treeView.jms;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class AlarmTopicSubscriber implements IMapMessageNotifier{
	
	Context jndiContext;
	IMapMessageNotifier messageNotifier;
	ConnectionFactory tcf;
	Connection tconn;
	Session tsess;
	MessageConsumer tsusc;
//	TopicSubscriber tsusc;
	MapListener listener;
	Topic topic; 
	String url="";
	String topicName="";
	String keyStore ="";
	String keyStorePassword ="";
	String trustStore ="";	
	String id;
	Hashtable<String,String> properties;
	
	public AlarmTopicSubscriber(String url){
		this();
		this.url = url;
	}
	
	public AlarmTopicSubscriber(String url, String keyStore, String keyStorePassword, String trustStore) {
		super();
		// TODO Auto-generated constructor stub
		this.url = url;
		this.keyStore = keyStore;
		this.keyStorePassword = keyStorePassword;
		this.trustStore = trustStore;
	}

	public IMapMessageNotifier getMessageNotifier() {
		return messageNotifier;
	}

	public void setMessageNotifier(IMapMessageNotifier messageNotifier) {
		this.messageNotifier = messageNotifier;
	}

	public AlarmTopicSubscriber(){
	}
	
	private void initializeContext() throws Exception{
		properties = new Hashtable<String,String>();
		//initialize protocol
        properties.put(Context.INITIAL_CONTEXT_FACTORY, 
        		"org.exolab.jms.jndi.InitialContextFactory");
        properties.put(Context.PROVIDER_URL, 
        		url);
		//TODO: secure connection is not yet supported!!! - for that you have to also get protocol name - itis a simple issue actually
		jndiContext = new InitialContext(properties);
	}
	
	public void openConnection(){
		try {
			initializeContext();
	//		tcf = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
			tcf = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
		} catch (NamingException ne) {
			// TODO Auto-generated catch block
			System.out.println("JNDI API problem.");
			ne.printStackTrace();
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			tconn = tcf.createConnection();
			tsess = tconn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			System.out.println("JMS problem while opening connection.");
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MapListener createListener(){
		if (messageNotifier == null) {messageNotifier = this;}
		listener= new MapListener(messageNotifier);
		return listener;
	}
	
	public void subscribe(String topicName){
		if (tsess == null){
			openConnection();
		}
		this.topicName = topicName;
		try {
			topic = (Topic) jndiContext.lookup(topicName);
			//TODO: polish this
			int r = (int)(Math.random()*1000.0);
			id = "hehehe"+String.valueOf(r);
			id = "hehehe";
			//DurableSubscriber is used on purpose!!! Try to use its functionality - this will enable you get messages even if tree view is not enabled
			tsusc = tsess.createConsumer(topic);
			//tsusc = tsess.createDurableSubscriber(topic,id);
			try {
				tsess.recover();
			} catch (JMSException e){
				System.out.println("Cannot recover session!");
			}
			tsusc.setMessageListener(createListener());
			tconn.start();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.out.println("Topic "+topicName+" probably not found or other NamingException.");
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();			
		}
	}
	
	public void resumeListening(){
		if (tconn == null){ openConnection();}
		try {
			tconn.start();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			System.out.println("JMS problem while resuming connection.");
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pauseListening(){
		try {
			tconn.stop();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			System.out.println("JMS problem while pausing connection.");
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection(){
		try {
			tconn.stop();
			tconn.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			System.out.println("JMS problem while closing connection.");
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notifyObject(MapMessage msg) {
		// TODO Auto-generated method stub
		System.out.println("Ignoring message.");
	}
}
