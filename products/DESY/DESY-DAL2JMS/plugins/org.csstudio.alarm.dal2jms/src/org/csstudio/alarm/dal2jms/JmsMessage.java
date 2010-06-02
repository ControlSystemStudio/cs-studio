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
package org.csstudio.alarm.dal2jms;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;

/**
 * Helper class to generate a JMS message.
 *
 * FIXME (mclausen, jpenning, bknerr) : JMS Message Refactoring
 *  - JMS Message is duplicated from interconnection server
 *  - Does not belong there either
 *  - Is not a message itself but rather a service (prepares, creates, sends messages),
 *    consider refactoring to OSGi service
 *  - refactor singleton pattern, if needed at all, to enum (like done here)
 *
 * @author Matthias Clausen
 *
 */
public enum JmsMessage {

    INSTANCE;

    private static final Logger LOG = CentralLogger.getInstance().getLogger(JmsMessage.class);

	public static final String JMS_TIME_TO_LIVE_ALARMS = "jmsTimeToLiveAlarms";
	public static final String JMS_TIME_TO_LIVE_LOGS = "jmsTimeToLiveLogs";
	public static final String JMS_TIME_TO_LIVE_PUT_LOGS = "jmsTimeToLivePutLogs";

	public static final String JMS_LOG_CONTEXT = "LOG";
	public static final String JMS_ALARM_CONTEXT = "ALARM";
	public static final String JMS_PUT_LOG_CONTEXT = "PUT_LOG";
	public static final String JMS_SNL_LOG_CONTEXT = "SNL_LOG";

	public static final String SEVERITY_NO_ALARM 	= "NO_ALARM";
	public static final String SEVERITY_MINOR 		= "MINOR";
	public static final String SEVERITY_MAJOR 		= "MAJOR";
	public static final String SEVERITY_INVALID 	= "INVALID";

	public static final String MESSAGE_TYPE_IOC_ALARM	= "ioc-alarm";
	public static final String MESSAGE_TYPE_EVENT		= "event";
	public static final String MESSAGE_TYPE_D3_ALARM	= "d3-alarm";
	public static final String MESSAGE_TYPE_STATUS		= "status";
	public static final String MESSAGE_TYPE_SIMULATOR	= "simulator";
	public static final String MESSAGE_TYPE_LOG	        = "log";

	/**
	 * TODO (mclausen) : anything?
	 *
	 * @author bknerr
	 * @author $Author$
	 * @version $Revision$
	 * @since 02.06.2010
	 */
	public enum JmsMessageType {
	    JMS_MESSAGE_TYPE_ALARM,
	    JMS_MESSAGE_TYPE_LOG,
	    JMS_MESSAGE_TYPE_PUT_LOG;

	}

	private ISharedConnectionHandle _sharedSenderConnection;

	private JmsMessage () {
		/*
		 * create JMS connection
		 */
		try {
			_sharedSenderConnection = createJmsConnection();
		} catch (final JMSException e) {
			// TODO (mclausen) : Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new JMS connection.
	 *
	 * @return the connection.
	 * @throws JMSException
	 *             if an error occurs.
	 */
	@CheckForNull
	public ISharedConnectionHandle createJmsConnection() throws JMSException {

		return SharedJmsConnections.sharedSenderConnection();
	}


	/**
	 * Creates a new JMS session.
	 *
	 * @return the session.
	 * @throws JMSException
	 *             if an error occurs.
	 */
	@CheckForNull
	public Session createJmsSession() throws JMSException {
	    if (_sharedSenderConnection != null) {
	        return _sharedSenderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    }
	    LOG.warn("Shared sender connection is null. Session could not be created.");
	    return null;
	}

	public void sendMessage(@Nonnull final JmsMessageType type, @Nonnull final MapMessage message) {

		/*
		 * get preferences
		 */
//		final IPreferencesService prefs = Platform.getPreferencesService();
//	    final String jmsTimeToLiveAlarms = prefs.getString(Activator.getDefault().getPluginId(),
//	    		JMS_TIME_TO_LIVE_ALARMS, "", null);
//	    final String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
//	    		JMS_TIME_TO_LIVE_LOGS, "", null);
//	    final String jmsTimeToLivePutLogs = prefs.getString(Activator.getDefault().getPluginId(),
//	    		JMS_TIME_TO_LIVE_PUT_LOGS, "", null);

//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, "3600000");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "600000");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, "3600000");

//        int jmsTimeToLiveAlarmsInt = Integer.parseInt(jmsTimeToLiveAlarms);
//		int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);
//		int jmsTimeToLivePutLogsInt = Integer.parseInt(jmsTimeToLivePutLogs);

		final int jmsTimeToLiveAlarmsInt = 3600000;
		final int jmsTimeToLiveLogsInt = 600000;
		final int jmsTimeToLivePutLogsInt = 3600000;

		Session session = null;
		try {
		    session = createJmsSession();
		    String jmsContext;
		    int jmsTimeToLive;
		    switch (type) {
		        case JMS_MESSAGE_TYPE_ALARM :
		            /*
		             * get JMS alarm connection from InterconnectionServer class
		             */
		            jmsContext = JMS_ALARM_CONTEXT;
		            jmsTimeToLive = jmsTimeToLiveAlarmsInt;
		            break;
		        case JMS_MESSAGE_TYPE_LOG :
		            /*
		             * get JMS alarm connection from InterconnectionServer class
		             */
		            jmsContext = JMS_LOG_CONTEXT;
		            jmsTimeToLive = jmsTimeToLiveLogsInt;
		            break;
		        case JMS_MESSAGE_TYPE_PUT_LOG :
		            /*
		             * get JMS alarm connection from InterconnectionServer class
		             */
		            jmsContext = JMS_PUT_LOG_CONTEXT;
		            jmsTimeToLive = jmsTimeToLivePutLogsInt;
		            break;
		        default :
		            jmsContext = null;
		            jmsTimeToLive = 0;
		            session = null;
		    }

		    if (session != null) {
		        // Create the destination (Topic or Queue)
		        final Destination destination = session.createTopic(jmsContext);
		        // Create a MessageProducer from the Session to the Topic or Queue
		        final MessageProducer sender = session.createProducer(destination);
		        sender.setDeliveryMode(DeliveryMode.PERSISTENT);
		        sender.setTimeToLive(jmsTimeToLive);
		        //	    	MapMessage message = prepareMessage( session.createMapMessage(), type, name, value, severity, status, host, facility, text);

		        sender.send(message);

		        //session.close();
		        sender.close();
		    }
		} catch(final JMSException jmse) {
			LOG.debug("IocChangeState : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
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
}
