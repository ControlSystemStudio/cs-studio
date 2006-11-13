package org.csstudio.alarm.table;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.csstudio.libs.jms.*;
import org.csstudio.libs.jms.preferences.*;
/**
 * Class used to establish connection with jms server
 */

public class MessageReceiver {

    Hashtable<String, String>   properties  = null;
    Context                     context     = null;
    ConnectionFactory           factory     = null;
    Connection                  connection  = null;
    Session                     session     = null;
    MessageConsumer             receiver    = null;
    TextMessage                 textMessage = null;
    Message                     message     = null;
    Topic		                destination = null;  // if ! topic: Destination
	
    /**
     * Parameter is listener, the one to be notified
     */        
	public void startListener(MessageListener listener) throws Exception{
        properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, 
        		JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.INITIAL_CONTEXT_FACTORY));
        properties.put(Context.PROVIDER_URL, 
        		JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.URL));
        context = new InitialContext(properties);
        factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = (Topic)context.lookup(JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.QUEUE));

        //
        // here we can decide whether we will get any messages regardless whether we are connected or not
        // for now we use here the only_when_online method
        //
        if (true) {
        	receiver = session.createConsumer(destination);
        } /*else {
        // create permanent connection:
        	receiver = session.createDurableSubscriber(destination, uniqueNameOfCssInstance);
        }*/
        connection.start();
       
        receiver.setMessageListener(listener);
	}

	/**
	 * Cleans up resources
	 */
	public void stopListening() throws Exception{
        receiver.close();
        session.close();
        connection.stop();
        connection.close();
        properties  = null;
        context     = null;
        factory     = null;
        connection  = null;
        session     = null;
        receiver    = null;
        textMessage = null;
        message     = null;
        destination = null;        
	}
	
	
	
}
