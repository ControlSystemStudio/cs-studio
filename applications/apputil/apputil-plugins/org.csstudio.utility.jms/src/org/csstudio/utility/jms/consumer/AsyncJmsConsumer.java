
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.utility.jms.consumer;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.csstudio.utility.jms.JmsUtilityException;
import org.csstudio.utility.jms.sharedconnection.ClientConnectionException;
import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;

/**
 * @author mmoeller
 * @version 1.0
 * @since 11.04.2012
 */
public class AsyncJmsConsumer {
    
    /** The connection handles that this class uses */
    private ISharedConnectionHandle[] handle;
    
    /** Array of JMS sessions */
    private Session[] session;
    
    private MessageConsumer[] consumer;

    public AsyncJmsConsumer() throws ClientConnectionException {
        try {
            handle = SharedJmsConnections.sharedReceiverConnections();
        } catch (JMSException e) {
            throw new ClientConnectionException(e);
        } catch (JmsUtilityException e) {
            throw new ClientConnectionException(e);
        }
    }
    
    public void createMessageConsumer(String topicName, boolean durable, String durableName)
            throws ClientConnectionException {
        
        int connectionCount = handle.length;
        session = new Session[connectionCount];
        consumer = new MessageConsumer[connectionCount];
        for(int i = 0;i < connectionCount;i++) {            
            try {
                session[i] = handle[i].createSession(false, Session.AUTO_ACKNOWLEDGE);
                if((durable == true) && (durableName != null)) {
                    consumer[i] = session[i]
                            .createDurableSubscriber(session[i].createTopic(topicName),
                                                     durableName);
                } else {
                    consumer[i] = session[i]
                            .createConsumer(session[i].createTopic(topicName));
                }
            } catch(JMSException jmse) {
                throw new ClientConnectionException("Cannot create message consumer.", jmse);
            }
        }
    }
    
    public void addMessageListener(MessageListener listener) throws JMSException {
        if (consumer != null) {
            for (MessageConsumer o : consumer) {
                if (o != null) {
                    o.setMessageListener(listener);
                }
            }
        }
    }
    
    public void removeMessageListener() throws JMSException {
        if (consumer != null) {
            for (MessageConsumer o : consumer) {
                if (o != null) {
                    o.setMessageListener(null);
                }
            }
        }
    }
    
    public void close() {
        for (MessageConsumer o : consumer) {
            if (o != null) {
                try{o.close();}catch(Exception e){/*Ignore Me*/}
            }
        }
        for (Session o : session) {
            if (o != null) {
                try{o.close();}catch(Exception e){/*Ignore Me*/}
            }
        }
        for (ISharedConnectionHandle o : handle) {
            if (o != null) {
                o.release();
            }
        }
    }
}
