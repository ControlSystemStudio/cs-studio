/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

    
    public void startSender(boolean acknowledge) throws Exception {
        properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, 
        		JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY));
        properties.put(Context.PROVIDER_URL, 
        		JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.SENDER_URL));
        context = new InitialContext(properties);
        factory = (ConnectionFactory) context.lookup("ConnectionFactory");
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        if(acknowledge == false) {
        	destination = (Destination) session.createTopic(JmsLogsPlugin.getDefault().getPluginPreferences().getString(AlarmViewerPreferenceConstants.QUEUE));
        } else {
        	destination = (Destination) session.createTopic("ACK");
        }
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
