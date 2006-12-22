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

import org.csstudio.platform.libs.jms.JmsPlugin;
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
