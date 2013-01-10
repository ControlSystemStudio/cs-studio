
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

package org.csstudio.alarm.jms2ora.util;

import java.util.Hashtable;

import javax.annotation.Nonnull;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Class used to establish connection with jms server
 */

public class JmsMessageReceiver {

    private final Hashtable<String, String> properties;
    private Context context;
    private ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private MessageConsumer[] receiver;
    private Topic destination;
    private final String[] topics;

    public JmsMessageReceiver(@Nonnull final String initialContextFactory,
                              @Nonnull final String providerURL,
                              @Nonnull final String[] topicArray) throws NamingException {

        properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory);
        properties.put(Context.PROVIDER_URL, providerURL);

        context = new InitialContext(properties);

        topics = topicArray;
    }

    /**
     * Parameter is listener, the one to be notified
     */
    public final void startListener(@Nonnull final MessageListener listener,
                                    @Nonnull final String uniqueId) throws Exception {

        factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        connection = factory.createConnection();
        connection.setClientID(uniqueId);

        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //
        // here we can decide whether we will get any messages regardless whether we are connected or not
        // for now we use here the only_when_online method
        //
        receiver = new MessageConsumer[topics.length];
        for (int i = 0;i < topics.length;i++) {

            destination = session.createTopic(topics[i]);

            // TODO
            // No durable subscription with ActiveMQ 4.x
            // Durable subscription setzen:
            // Die Unique ID darf nicht die Hashnumber beinhalten, weil sonst immer
            // neue Subscriber erzeugt werden.
            // receiver[i] = session.createDurableSubscriber(destination, uniqueId + "_" + topics[i]);
            receiver[i] = session.createConsumer(destination);
            receiver[i].setMessageListener(listener);
        }
    }

    /**
     * Cleans up resources
     */
    public final void stopListening() {

        if (receiver != null) {
            for (final MessageConsumer r: receiver) {
                if(r != null) {
                    try{r.close();}catch(final JMSException e){/* Can be ignored */}
                }
            }
        }

        if(session != null) {
            try{session.close();}catch(final JMSException jmse){/* Can be ignored */}
            session = null;
        }

        if(connection != null) {
            try{connection.stop();}catch(final JMSException jmse){/* Can be ignored */}
            try{connection.close();}catch(final JMSException jmse){/* Can be ignored */}
            connection = null;
        }

        //properties  = null;

        if(context != null) {
            try {
                context.close();
            } catch(final NamingException e) {
                /* Can be ignored */
            }
            context = null;
        }

        //properties  = null;
        factory = null;
        connection = null;
        session = null;
        receiver = null;
        destination = null;
    }
}
