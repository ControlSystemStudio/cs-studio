
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.ams.alarmchainmanager.jms;

import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.csstudio.ams.Log;

/**
 * @author Markus Moeller
 *
 */
public class JmsReplyMessageSender
{
    /** Context for JMS connections. */
    private Context amsSenderContext = null;
    
    /** Factory for JMS connections */
    private ConnectionFactory amsSenderFactory = null;
    
    /** JMS connection */
    private Connection amsSenderConnection = null;

    /** JMS session */
    private Session amsSenderSession = null;
    
    /** JMS producer / sender */
    private MessageProducer amsPublisherReply = null;

    /**  */
    private boolean initalized = false;
    
    public JmsReplyMessageSender(String clientId, String url,
                                 String connectionFactoryClass, String connectionFactory) {
        
        Hashtable<String, String> properties = null;
        
        try {
            properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, connectionFactoryClass);
            properties.put(Context.PROVIDER_URL, url);
            amsSenderContext = new InitialContext(properties);
            
            amsSenderFactory = (ConnectionFactory) amsSenderContext.lookup(connectionFactory);
            amsSenderConnection = amsSenderFactory.createConnection();
            
            amsSenderConnection.setClientID("SmsConnectorWorkSenderInternal");
                        
            amsSenderSession = amsSenderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            amsSenderConnection.start();
            
            initalized = true;
        } catch(Exception e) {
            Log.log(this, Log.FATAL, "could not init internal Jms", e);
            initalized = false;
        }
    }
    
    public boolean isInitialized() {
        return initalized;
    }
    
    public void createProducer(String topic) {
        try {
            amsPublisherReply = amsSenderSession.createProducer(amsSenderSession.createTopic(topic));
        } catch(JMSException jmse) {
            Log.log(this, Log.FATAL, "Could not create publisher: " + jmse.getMessage());
            
            amsPublisherReply = null;
        }
    }
    
    public boolean existsPublisher() {
        return (amsPublisherReply != null);
    }
    
    public void sendMapMessage(MapMessage message) throws JMSException, NullPointerException {
        if(amsPublisherReply != null) {
            try {
                amsPublisherReply.send(message);
            } catch(JMSException jmse) {
                Log.log(this, Log.FATAL, "Could not send message: " + jmse.getMessage());
                throw jmse;
            }
        } else {
            throw new NullPointerException("JMS sender is not valid");
        }
    }
    
    public MapMessage createMapMessage() {
        
        MapMessage msg = null;
        
        if(amsSenderSession != null) {
            try {
                msg = amsSenderSession.createMapMessage();
            } catch(JMSException jmse) {
                // Can be ignored
            }
        }
        
        return msg;
    }
}
