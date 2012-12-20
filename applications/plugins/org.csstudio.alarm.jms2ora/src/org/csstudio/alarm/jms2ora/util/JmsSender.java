
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
 *
 */

package org.csstudio.alarm.jms2ora.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.annotation.Nonnull;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class JmsSender {

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(JmsSender.class);

    /** AMS date format */
    private static final String AMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private Hashtable<String, String> properties;
    private Context context;
    private ConnectionFactory factory;
    private Connection connection;
    private Session session;
    private Destination dest;
    private MessageProducer sender;
    private final String jmsUrl;
    private final String jmsTopic;

    public JmsSender(final String url, final String topic) {

        jmsUrl = url;
        jmsTopic = topic;

        properties = new Hashtable<String, String>();

        // Set the properties for the context
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.put(Context.PROVIDER_URL, jmsUrl);

        try {

            // Create a context
            context = new InitialContext(properties);

            // Create a connection factory
            factory = (ConnectionFactory)context.lookup("ConnectionFactory");

            // Create a connection
            connection = factory.createConnection();

            // Set client id
            connection.setClientID("Jms2OraJmsSender@" + Environment.getInstance().getHostName());

            // Start the connection
            connection.start();

            // Create a session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            dest = session.createTopic(jmsTopic);

            // Create a message producer
            sender = session.createProducer(dest);
        } catch(final NamingException ne) {
            LOG.info(" *** NamingException *** : " + ne.getMessage());
            closeAll();
        } catch(final JMSException jmse) {
            LOG.info(" *** JMSException *** : " + jmse.getMessage());
            closeAll();
        }
    }

    public final boolean sendMessage(final String type, final String messageText, final String severity) {
        return sendMessage(type, null, messageText, severity, null);
    }

    public final boolean sendMessage(final String type, final String name, final String messageText, final String severity) {
        return sendMessage(type, name, messageText, severity, null);
    }

    public final boolean sendMessage(final String type, final String name, final String messageText,
                               final String severity, final String destination) {

        MapMessage message = null;
        boolean result = false;

        try {

            message = this.createMapMessage();
            if(message != null) {
                message.setString("TYPE", type);
                message.setString("EVENTTIME", createTimeString());
                message.setString("TEXT", messageText);
                message.setString("USER", Environment.getInstance().getUserName());
                message.setString("HOST", Environment.getInstance().getHostName());
                message.setString("APPLICATION-ID", "Jms2Ora");

                if(severity != null) {
                    message.setString("SEVERITY", severity);
                }

                if(destination != null) {
                    message.setString("DESTINATION", destination);
                }

                if(name != null) {
                    message.setString("NAME", name);
                }

                // Send the message
                sender.send(message, DeliveryMode.PERSISTENT, 1, 0);

                // Clean up
                clearMessage(message);
                message = null;

                LOG.info(" JmsSender.sendMessage(): *** JMS DONE ***.");

                result = true;
            } else {
                LOG.info(" JmsSender.sendMessage(): *** JMS NOT DONE *** : MapMessage not created.");
            }
        } catch(final JMSException jmse) {
            LOG.info(" JmsSender.sendMessage(): *** JMSException *** : " + jmse.getMessage());
            return false;
        }

        return result;
    }

    public final MapMessage createMapMessage() {

        MapMessage message = null;

        if(session != null) {
            try {
                message = session.createMapMessage();
            } catch(final JMSException jmse) {
                /* Can be ignored */
            }
        }

        return message;
    }

    public final MapMessage createMapMessageFromHashtable(final Hashtable<String, String> messageContent) {

        MapMessage message = null;
        String key = null;

        if(session == null) {
            return null;
        }

        try {

            message = session.createMapMessage();

            final Enumeration<String> keys = messageContent.keys();
            while(keys.hasMoreElements()) {
                key = keys.nextElement();
                message.setString(key, messageContent.get(key));
            }
        } catch(final JMSException jmse) {
            message = null;
        }

        return message;
    }

    public final void clearMessage(final MapMessage message) {

        try {
            message.clearBody();
            message.clearProperties();
        } catch(final JMSException e) {/* Can be ignored */}
    }

    public final void closeAll() {
        if(sender!=null){try{sender.close();}catch(final Exception e){/* Can be ignored */}sender=null;}
        dest = null;
        if(session!=null){try{session.close();}catch(final Exception e){/* Can be ignored */}session=null;}
        if(connection!=null){try{connection.stop();}catch(final Exception e){/* Can be ignored */}}
        if(connection!=null){try{connection.close();}catch(final Exception e){/* Can be ignored */}connection=null;}
        factory = null;
        if(context!=null){try{context.close();}catch(final Exception e){/* Can be ignored */}context=null;}
        if(properties != null) {
            properties.clear();
            properties = null;
        }
    }

    public final boolean isConnected() {
        return connection != null;
    }

    /**
     * Creates date and time for the JMS message.
     *
     * @return String with the date and time
     */
    @Nonnull
    private String createTimeString() {
        final SimpleDateFormat format = new SimpleDateFormat(AMS_DATE_FORMAT);
        return format.format(Calendar.getInstance().getTime());
    }
}
