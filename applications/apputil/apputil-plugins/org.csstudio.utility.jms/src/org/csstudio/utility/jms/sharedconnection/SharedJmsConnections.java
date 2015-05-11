
/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.jms.sharedconnection;

import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.csstudio.utility.jms.JmsUtilityException;
import org.csstudio.utility.jms.SharedReceiverConnectionService;
import org.csstudio.utility.jms.SharedSenderConnectionService;

/**
 * Utility class for using the Shared JMS Connection Services.
 *
 * @author Joerg Rathlev
 */
public final class SharedJmsConnections {

	private static SharedSenderConnectionService senderConnectionService = null;
	private static SharedReceiverConnectionService receiverConnectionService = null;

	private static String publisherUrl = null;
	private static String publisherClientId = null;
	
    private static String consumerUrl1 = null;
    private static String consumerUrl2 = null;
    private static String consumerClientId = null;

    // Private constructor to prevent instantiation.
	private SharedJmsConnections() {
	    // Empty
	}

	/**
	 * Sets the URL and the client id for the shared publisher connection.
	 * The client id may be null.
	 * 
	 * @param url - URL of the JMS server
	 * @param id - The client id that is used for the connection. May be null!
	 */
	public static void staticInjectPublisherUrlAndClientId(String url, String id) {
	    if (publisherUrl == null) {
	        publisherUrl = url;
	    }
	    if (publisherClientId == null) {
	        publisherClientId = id;
	    }
	}
	
	/**
	 * Sets the URL for the shared publisher connection.
	 * 
	 * @param url - URL of the JMS server
	 */
	public static void staticInjectPublisherUrl(String url) {
		staticInjectPublisherUrlAndClientId(url, null);
	}
	
	/**
	 * Sets the URL and the client id for the shared receiver connection.
	 * The client id may be null.
	 * 
	 * @param url1 - URL of the first JMS server.
	 * @param url2 - URL of the second JMS server.
	 * @param id - The client id that is used for the connection. May be null!
	 */
    public static void staticInjectConsumerUrlAndClientId(String url1, String url2, String id) {
        if (consumerUrl1 == null) {
            consumerUrl1 = url1;
        }
        if (consumerUrl2 == null) {
            consumerUrl2 = url2;
        }
        if (consumerClientId == null) {
            consumerClientId = id;
        }
    }
	
	/**
	 * Sets the URL for the shared receiver connection.
	 * 
	 * @param url1 - URL of the first JMS server.
	 * @param url2 - URL of the second JMS server.
	 */
    public static void staticInjectConsumerUrl(String url1, String url2) {
    	staticInjectConsumerUrlAndClientId(url1, url2, null);
    }
    
	/**
	 * Returns a handle to a shared JMS connection for sending JMS messages. If
	 * the shared connection has not been created yet, this method will create
	 * and start the connection before it returns.
	 *
	 * @return a handle to a shared JMS connection for sending JMS messages.
	 * @throws JMSException
	 *             if the shared connection could not be created or started due
	 *             to an internal error.
	 */
	public static synchronized ISharedConnectionHandle sharedSenderConnection()
			throws JMSException, JmsUtilityException {
	    
	    if (publisherUrl == null) {
	        throw new JmsUtilityException("The publisher URL is null. Call static method staticInjectPublisherUrlAndClientId() before you try to get a connection handle.");
	    }
	    
	    if (senderConnectionService == null) {
	        senderConnectionService =
	                new SharedSenderConnectionService(publisherUrl, publisherClientId);
	    }
	    
		return senderConnectionService.sharedConnection();
	}

	/**
	 * Returns handles to the shared JMS connections for receiving JMS messages.
	 * If the shared connections have not been created yet, this method will
	 * create and start the connections before it returns.
	 *
	 * @return handles to the shared connections.
	 * @throws JMSException
	 *             if one of the underlying shared connections could not be
	 *             created or started due to an internal error.
	 */
	public static synchronized ISharedConnectionHandle[] sharedReceiverConnections()
			throws JMSException, JmsUtilityException {
		
	    if ((consumerUrl1 == null) || (consumerUrl2 == null)) {
	        throw new JmsUtilityException("At least one consumer URL is null. Call static method staticInjectConsumerUrlAndClientId() before you try to get a connection handle.");
	    }

	    if (receiverConnectionService == null) {
	        receiverConnectionService =
	                new SharedReceiverConnectionService(consumerUrl1, consumerUrl2, consumerClientId);
	    }
	    
	    return receiverConnectionService.sharedConnections();
	}

	/**
	 * Starts a message listener that will listen on the shared receiver
	 * connections.
	 *
	 * @param listener
	 *            the listener.
	 * @param topics
	 *            the topics to subscribe to.
	 * @param acknowledgeMode
	 *            the JMS session acknowledgement mode. Legal values are
	 *            <code>Session.AUTO_ACKNOWLEDGE</code>,
	 *            <code>Session.CLIENT_ACKNOWLEDGE</code>, and
	 *            <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
	 * @return An <code>IMessageListenerSession</code> which can be used to
	 *         control the listener session.
	 * @throws JMSException
	 *             if an internal error occured in the underlying JMS provider.
	 */
	public static IMessageListenerSession startMessageListener(
			final MessageListener listener, final String[] topics, final int acknowledgeMode)
			throws JMSException {
		if ((listener == null) || (topics == null) || (topics.length == 0)) {
			throw new IllegalArgumentException();
		}
		return receiverConnectionService.startSharedConnectionMessageListener(
				listener, topics, acknowledgeMode);
	}
}
