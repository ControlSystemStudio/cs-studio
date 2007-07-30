package org.csstudio.alarm.table.jms;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;


import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.AlarmViewerPreferenceConstants;
import org.csstudio.platform.libs.jms.JmsPlugin;
import org.csstudio.platform.libs.jms.preferences.PreferenceConstants;


public class SendMapMessage {
    
	Hashtable<String, String>   properties  = null;
    Context                     context     = null;
    ConnectionFactory           factory     = null;
    Connection                  connection  = null;
    Session                     session     = null;
    MessageProducer             sender      = null;
    Destination                 destination = null;
    MapMessage                  message     = null;

	public void startSender() throws Exception{
        properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, 
        		JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY));
        properties.put(Context.PROVIDER_URL, 
        		JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.SENDER_URL));
        context = new InitialContext(properties);
        factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // CHANGED BY: Markus Möller, 25.05.2007
        /*
        destination = (Destination)context.lookup(JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.QUEUE));
		*/
        
        destination = (Destination)session.createTopic(JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.QUEUE));

        connection.start();
        sender = session.createProducer(destination);
        sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}
	
	public void stopSender() throws Exception{
		sender.close();
		session.close();
		connection.stop();
		connection.close();
		properties  = null;
		context     = null;
		factory     = null;
		connection  = null;
		session     = null;
		sender      = null;
		destination = null;
		message     = null;		
	}
	
	public MapMessage getSessionMessageObject() throws Exception{
		if(message != null) message.clearBody();
		else message = session.createMapMessage();
		return message;
	}
	
	public void sendMessage() throws Exception{
        sender.send(message);
	}
}
