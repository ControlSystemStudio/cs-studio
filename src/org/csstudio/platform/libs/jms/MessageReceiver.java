/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.platform.libs.jms;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.csstudio.platform.libs.jms.preferences.PreferenceConstants;
/**
 * Class used to establish connection with jms server
 */

public class MessageReceiver {

    Hashtable<String, String>   properties  = null;
    Context                     context     = null;
    ConnectionFactory           factory     = null;
    Connection                  connection  = null;
    Session                     session     = null;
    MessageConsumer[]           receiver    = null;
    TextMessage                 textMessage = null;
    Message                     message     = null;
    Topic		                destination = null;  // if ! topic: Destination
	private String[] queues;


    public MessageReceiver() throws NamingException{
        properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY,
        		JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.INITIAL_CONTEXT_FACTORY));
        properties.put(Context.PROVIDER_URL,
        		JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.URL));
        context = new InitialContext(properties);
//        destination = (Topic) context.lookup(JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.QUEUE).split(",")[0]);
        queues = JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.QUEUE).split(",");
    }

    public MessageReceiver(String initialContextFactory, String providerURL, String[] queues)throws NamingException{
        properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory);
        properties.put(Context.PROVIDER_URL,providerURL);
        context = new InitialContext(properties);
//        destination = (Topic)context.lookup(queues[0]);
        this.queues = queues;
    }

    /**
     * Parameter is listener, the one to be notified
     */
	public void startListener(MessageListener listener) throws Exception{

        factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //
        // here we can decide whether we will get any messages regardless whether we are connected or not
        // for now we use here the only_when_online method
        //
        receiver = new MessageConsumer[queues.length];
        for (int i=0;i<queues.length;i++){
        	/*
        	 * changed from OpenJMS to ActiveMQ
        	 * MCL 2007-05-23
        	 */
        	//destination = (Topic)context.lookup(queues[i]);
        	destination = session.createTopic(queues[i]);
        	receiver[i] = session.createConsumer(destination);
        	receiver[i].setMessageListener(listener);
        }
        	/*else {
        // create permanent connection:
        	receiver = session.createDurableSubscriber(destination, uniqueNameOfCssInstance);
        }*/
        connection.start();

//        receiver.setMessageListener(listener);
	}

	/**
	 * Cleans up resources
	 */
	public void stopListening() throws Exception{
		for (MessageConsumer r: receiver) {
			r.close();
			r=null;
		}
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
