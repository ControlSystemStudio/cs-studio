package org.csstudio.alarm.dal2jms;
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

import java.text.SimpleDateFormat;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

//import org.csstudio.diag.interconnectionServer.Activator;
//import org.csstudio.diag.interconnectionServer.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Helper class to generate a JMS message.
 * 
 * @author Matthias Clausen
 *
 */
public class JmsMessage {
	
	private static JmsMessage thisMessage = null;
	
	public static final String JMS_TIME_TO_LIVE_ALARMS = "jmsTimeToLiveAlarms";
	public static final String JMS_TIME_TO_LIVE_LOGS = "jmsTimeToLiveLogs";
	public static final String JMS_TIME_TO_LIVE_PUT_LOGS = "jmsTimeToLivePutLogs";
	
	public static final String JMS_LOG_CONTEXT = "LOG";
	public static final String JMS_ALARM_CONTEXT = "ALARM";
	public static final String JMS_PUT_LOG_CONTEXT = "PUT_LOG";
	public static final String JMS_SNL_LOG_CONTEXT = "SNL_LOG";
	
	public final static String SEVERITY_NO_ALARM 	= "NO_ALARM";
	public final static String SEVERITY_MINOR 		= "MINOR";
	public final static String SEVERITY_MAJOR 		= "MAJOR";
	public final static String SEVERITY_INVALID 	= "INVALID";
	
	public final static String MESSAGE_TYPE_IOC_ALARM	= "ioc-alarm";
	public final static String MESSAGE_TYPE_EVENT		= "event";
	public final static String MESSAGE_TYPE_D3_ALARM	= "d3-alarm";
	public final static String MESSAGE_TYPE_STATUS		= "status";
	public final static String MESSAGE_TYPE_SIMULATOR	= "simulator";
	public final static String MESSAGE_TYPE_LOG	        = "log";
	
	public final static int	JMS_MESSAGE_TYPE_ALARM		= 1;
	public final static int	JMS_MESSAGE_TYPE_LOG		= 2;
	public final static int	JMS_MESSAGE_TYPE_PUT_LOG	= 3;
	
	public ISharedConnectionHandle _sharedSenderConnection;
	
	public JmsMessage () {
		/*
		 * create JMS connection
		 */
		try {
			this._sharedSenderConnection = createJmsConnection();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JmsMessage getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisMessage == null) {
			synchronized (JmsMessage.class) {
				if (thisMessage == null) {
					thisMessage = new JmsMessage();
				}
			}
		}
		return thisMessage;
	}
	
	/**
	 * Creates a new JMS connection.
	 * 
	 * @return the connection.
	 * @throws JMSException
	 *             if an error occurs.
	 */
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
	public Session createJmsSession() throws JMSException {
		
		return this._sharedSenderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	public void sendMessage ( int messageType, MapMessage message) {
		
		String jmsContext = null;
		Session session = null;
		int jmsTimeToLive = 0;
		/*
		 * get preferences
		 */
		IPreferencesService prefs = Platform.getPreferencesService();
	    String jmsTimeToLiveAlarms = prefs.getString(Activator.getDefault().getPluginId(),
	    		JMS_TIME_TO_LIVE_ALARMS, "", null);  
	    String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		JMS_TIME_TO_LIVE_LOGS, "", null);  
	    String jmsTimeToLivePutLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		JMS_TIME_TO_LIVE_PUT_LOGS, "", null);  
	    
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, "3600000");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "600000");
//		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, "3600000");
	    
//        int jmsTimeToLiveAlarmsInt = Integer.parseInt(jmsTimeToLiveAlarms);
//		int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);
//		int jmsTimeToLivePutLogsInt = Integer.parseInt(jmsTimeToLivePutLogs);
		
		int jmsTimeToLiveAlarmsInt = 3600000;
		int jmsTimeToLiveLogsInt = 600000;
		int jmsTimeToLivePutLogsInt = 3600000;
		
		try {

			if ( messageType == JMS_MESSAGE_TYPE_ALARM) {
				/*
				 * get JMS alarm connection from InterconnectionServer class
				 */
				session = createJmsSession();
				jmsContext = JMS_ALARM_CONTEXT;
				jmsTimeToLive = jmsTimeToLiveAlarmsInt;
			} else if ( messageType == JMS_MESSAGE_TYPE_LOG) {
				/*
				 * get JMS alarm connection from InterconnectionServer class
				 */
				session = createJmsSession();
				jmsContext = JMS_LOG_CONTEXT;
				jmsTimeToLive = jmsTimeToLiveLogsInt;
			} else if ( messageType == JMS_MESSAGE_TYPE_PUT_LOG) {
				/*
				 * get JMS alarm connection from InterconnectionServer class
				 */
				session = createJmsSession();
				jmsContext = JMS_PUT_LOG_CONTEXT;
				jmsTimeToLive = jmsTimeToLivePutLogsInt;
			}
		
	        // Create the destination (Topic or Queue)
			Destination destination = session.createTopic( jmsContext);

	        // Create a MessageProducer from the Session to the Topic or Queue
	    	MessageProducer sender = session.createProducer( destination);
	    	sender.setDeliveryMode( DeliveryMode.PERSISTENT);
	    	sender.setTimeToLive( jmsTimeToLive);

//	    	MapMessage message = prepareMessage( session.createMapMessage(), type, name, value, severity, status, host, facility, text);

			sender.send(message);
			
			//session.close();
			sender.close();
		}

		catch(JMSException jmse)
        {
			CentralLogger.getInstance().debug(this,"IocChangeState : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
        } finally {
        	if (session != null) {
        		try {
					session.close();
				} catch (JMSException e) {
					CentralLogger.getInstance().warn(this, "Failed to close JMS session", e);
				}
        	}
        }
	}
}
