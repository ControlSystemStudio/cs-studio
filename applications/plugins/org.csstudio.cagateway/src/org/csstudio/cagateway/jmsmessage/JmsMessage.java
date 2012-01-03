
/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.cagateway.jmsmessage;

import java.text.SimpleDateFormat;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.cagateway.PreferenceProperties;
import org.csstudio.cagateway.preferences.CAGatewayPreference;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to generate a JMS message.

 * FIXME (mclausen, jpenning, bknerr) : JMS Message Refactoring
 *  - JMS Message has been duplicated to dal2jms
 *  - Does not belong there either
 *  - Is not a message itself but rather a service (prepares, creates, sends messages),
 *    consider refactoring to OSGi service
 *  - refactor singleton pattern, if needed at all, to enum (like done there)
 *  - don't use static final ints (that's c code), use enums
 *
 * @author Matthias Clausen
 *
 */
public enum JmsMessage {


    INSTANCE;

	public static final String SEVERITY_NO_ALARM 	= "NO_ALARM";
	public static final String SEVERITY_MINOR 		= "MINOR";
	public static final String SEVERITY_MAJOR 		= "MAJOR";
	public static final String SEVERITY_INVALID 	= "INVALID";

	public static final String STATUS_NO_ALARM 		= "NO_ALARM";
	public static final String STATUS_HIHI_ALARM	= "HIHI_ALARM";
	public static final String STATUS_HIGH_ALARM 	= "HIGH_ALARM";
	public static final String STATUS_LOLO_ALARM 	= "LOLO_ALARM";
	public static final String STATUS_LOW_ALARM 	= "LOW_ALARM";
	public static final String STATUS_HW_LIMIT_ALARM= "HW_LIMIT_ALARM";
	public static final String STATUS_STATE_ALARM	= "STATE_ALARM";

	public static final String MESSAGE_TYPE_IOC_ALARM	= "ioc-alarm";
	public static final String MESSAGE_TYPE_GW_ALARM	= "gw-alarm";
	public static final String MESSAGE_TYPE_EVENT		= "event";
	public static final String MESSAGE_TYPE_D3_ALARM	= "d3-alarm";
	public static final String MESSAGE_TYPE_STATUS		= "status";
	public static final String MESSAGE_TYPE_SIMULATOR	= "simulator";
	public static final String MESSAGE_TYPE_LOG	        = "log";

	public static final int	JMS_MESSAGE_TYPE_ALARM		= 1;
	public static final int	JMS_MESSAGE_TYPE_LOG		= 2;
	public static final int	JMS_MESSAGE_TYPE_PUT_LOG	= 3;

	private ISharedConnectionHandle _sharedSenderConnection;

	private static final Logger LOG = LoggerFactory.getLogger(JmsMessage.class);

	private JmsMessage () {
		/*
		 * nothing to do
		 */
	}

	public void sendMessage ( final int messageType, final String type, final String name, final String value, final String severity, final String status, final String host, final String facility, final String text) {

		String jmsContext = null;
		Session session = null;
		int jmsTimeToLive = 0;
		/*
		 * get preferences
		 */
	    final Integer jmsTimeToLiveAlarms = CAGatewayPreference.JMS_TIME_TO_LIVE_ALARMS.getValue();
	    final Integer jmsTimeToLiveLogs = CAGatewayPreference.JMS_TIME_TO_LIVE_LOGS.getValue();
	    final Integer jmsTimeToLivePutLogs = CAGatewayPreference.JMS_TIME_TO_LIVE_PUT_LOGS.getValue();

		try {

			if ( messageType == JMS_MESSAGE_TYPE_ALARM) {
				/*
				 * get JMS alarm connection from InterconnectionServer class
				 */
				session = createJmsSession();
				jmsContext = PreferenceProperties.JMS_ALARM_CONTEXT;
				jmsTimeToLive = jmsTimeToLiveAlarms;
			} else if ( messageType == JMS_MESSAGE_TYPE_LOG) {
				/*
				 * get JMS alarm connection from InterconnectionServer class
				 */
				session = createJmsSession();
				jmsContext = PreferenceProperties.JMS_LOG_CONTEXT;
				jmsTimeToLive = jmsTimeToLiveLogs;
			} else if ( messageType == JMS_MESSAGE_TYPE_PUT_LOG) {
				/*
				 * get JMS alarm connection from InterconnectionServer class
				 */
				session = createJmsSession();
				jmsContext = PreferenceProperties.JMS_PUT_LOG_CONTEXT;
				jmsTimeToLive = jmsTimeToLivePutLogs;
			}

	        // Create the destination (Topic or Queue)
			final Destination destination = session.createTopic( jmsContext);

	        // Create a MessageProducer from the Session to the Topic or Queue
	    	final MessageProducer sender = session.createProducer( destination);
	    	sender.setDeliveryMode( DeliveryMode.PERSISTENT);
	    	sender.setTimeToLive( jmsTimeToLive);

	    	final MapMessage message = prepareMessage( session.createMapMessage(), type, name, value, severity, status, host, facility, text);

			sender.send(message);

			//session.close();
			sender.close();
		}

		catch(final JMSException jmse)
        {
//			InterconnectionServer.getInstance().countJmsSendMessageErrorAndReconnectIfTooManyErrors();
			LOG.debug("IocChangeState : send ALARM message : *** EXCEPTION *** : {}", jmse.getMessage());
        } finally {
        	if (session != null) {
        		try {
					session.close();
				} catch (final JMSException e) {
					LOG.warn("Failed to close JMS session", e);
				}
        	}
        }
	}

	private MapMessage prepareMessage ( final MapMessage message, final String type, final String name, final String value, final String severity, final String status, final String host, final String facility, final String text) {
		/*
		 * typical entries:
		 * type = ioc-alarm
		 * name = Localhost:logicalIocName:connectState
		 * value = NOT_CONNECTED
		 * severity = MAJOR
		 *
		 * local entries:
		 * EVENTTIME
		 */

		try {
			if ( type != null) {
				message.setString( "TYPE", type);
			} else {
				message.setString( "TYPE", "event");
			}
			if ( name != null) {
				message.setString( "NAME", name);
			} else {
				message.setString( "NAME", "NOT_SPEC");
			}
			if ( value != null) {
				message.setString( "VALUE", value);
			} // else not set
			if ( severity != null) {
				message.setString( "SEVERITY", severity);
			} else {
				message.setString( "SEVERITY", SEVERITY_NO_ALARM);
			}
			if ( status != null) {
				message.setString( "STATUS", status);
			} // else not set
			if ( host != null) {
				message.setString( "HOST", host);
			} // else not set
			if ( facility != null) {
				message.setString( "FACILITY", facility);
			} // else not set
			if ( text != null) {
				message.setString( "TEXT", text);
			} // else not set

			//
			// create time stamp
			// this is a copy from the class ClientRequest
			//
			final SimpleDateFormat sdf = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT);
	        final java.util.Date currentDate = new java.util.Date();
	        final String eventTime = sdf.format(currentDate);
	        message.setString( "EVENTTIME", eventTime);

		}
	    catch(final JMSException jmse)
	    {
	    	LOG.debug("IocChangeState : prepareJmsMessage : *** EXCEPTION *** : {}", jmse.getMessage());
	    }
		return message;
	}
	public Session createJmsSession() throws JMSException {
		if ( _sharedSenderConnection == null) {
			_sharedSenderConnection = SharedJmsConnections.sharedSenderConnection();
		}
		return _sharedSenderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
}
