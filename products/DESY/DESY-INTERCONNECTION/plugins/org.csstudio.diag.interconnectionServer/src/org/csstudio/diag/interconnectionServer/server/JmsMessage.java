package org.csstudio.diag.interconnectionServer.server;

import java.text.SimpleDateFormat;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.csstudio.diag.interconnectionServer.Activator;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class JmsMessage {
	
	private static JmsMessage thisMessage = null;
	
	public final static String SEVERITY_NO_ALARM 	= "NO_ALARM";
	public final static String SEVERITY_MINOR 		= "MINOR";
	public final static String SEVERITY_MAJOR 		= "MAJOR";
	public final static String SEVERITY_INVALID 	= "INVALID";
	
	public final static String MESSAGE_TYPE_IOC_ALARM	= "ioc-alarm";
	public final static String MESSAGE_TYPE_EVENT		= "event";
	public final static String MESSAGE_TYPE_D3_ALARM	= "d3-alarm";
	public final static String MESSAGE_TYPE_STATUS		= "status";
	
	public final static int	JMS_MESSAGE_TYPE_ALARM		= 1;
	public final static int	JMS_MESSAGE_TYPE_LOG		= 2;
	public final static int	JMS_MESSAGE_TYPE_PUT_LOG	= 3;
	
	public JmsMessage () {
		/*
		 * nothing to do
		 */
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
	
	public void sendMessage ( int messageType, String type, String name, String value, String severity, String status, String host, String facility, String text, String howTo) {
		
		String jmsContext = null;
		Connection connection = null;
		int jmsTimeToLive = 0;
		/*
		 * get preferences
		 */
		IPreferencesService prefs = Platform.getPreferencesService();
	    String jmsTimeToLiveAlarms = prefs.getString(Activator.getDefault().getPluginId(),
	    		"jmsTimeToLiveAlarms", "", null);  
	    String jmsTimeToLiveLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		"jmsTimeToLiveLogs", "", null);  
	    String jmsTimeToLivePutLogs = prefs.getString(Activator.getDefault().getPluginId(),
	    		"jmsTimeToLivePutLogs", "", null);  
	    
        int jmsTimeToLiveAlarmsInt = Integer.parseInt(jmsTimeToLiveAlarms);
		int jmsTimeToLiveLogsInt = Integer.parseInt(jmsTimeToLiveLogs);
		int jmsTimeToLivePutLogsInt = Integer.parseInt(jmsTimeToLivePutLogs);
		
		if ( messageType == JMS_MESSAGE_TYPE_ALARM) {
			/*
			 * get JMS alarm connection from InterconnectionServer class
			 */
			connection = InterconnectionServer.getInstance().getAlarmConnection();
			jmsContext = PreferenceProperties.JMS_ALARM_CONTEXT;
			jmsTimeToLive = jmsTimeToLiveAlarmsInt;
		} else if ( messageType == JMS_MESSAGE_TYPE_LOG) {
			/*
			 * get JMS alarm connection from InterconnectionServer class
			 */
			connection = InterconnectionServer.getInstance().getLogConnection();
			jmsContext = PreferenceProperties.JMS_LOG_CONTEXT;
			jmsTimeToLive = jmsTimeToLiveLogsInt;
		} else if ( messageType == JMS_MESSAGE_TYPE_PUT_LOG) {
			/*
			 * get JMS alarm connection from InterconnectionServer class
			 */
			connection = InterconnectionServer.getInstance().getPutLogConnection();
			jmsContext = PreferenceProperties.JMS_PUT_LOG_CONTEXT;
			jmsTimeToLive = jmsTimeToLivePutLogsInt;
		}
		
		try {
			
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	        // Create the destination (Topic or Queue)
			Destination destination = session.createTopic( jmsContext);

	        // Create a MessageProducer from the Session to the Topic or Queue
	    	MessageProducer sender = session.createProducer( destination);
	    	sender.setDeliveryMode( DeliveryMode.PERSISTENT);
	    	sender.setTimeToLive( jmsTimeToLive);

	    	MapMessage message = prepareMessage( session.createMapMessage(), type, name, value, severity, status, host, facility, text, howTo);

			sender.send(message);
			
			//session.close();
			sender.close();
		}

		catch(JMSException jmse)
        {
			InterconnectionServer.getInstance().checkSendMessageErrorCount();
			CentralLogger.getInstance().debug(this,"IocChangeState : send ALARM message : *** EXCEPTION *** : " + jmse.getMessage());
        }
	}
	
	public MapMessage prepareMessage ( MapMessage message, String type, String name, String value, String severity, String status, String host, String facility, String text, String howTo) {
		/*
		 * typical entries:
		 * type = ioc-alarm
		 * name = Localhost:logicalIocName:connectState
		 * value = NOT_CONNECTED
		 * severity = MAJOR
		 * howto = ??
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
			if ( howTo != null) {
				message.setString( "HOWTO", howTo );
			} // else not set
			
			//
			// create time stamp 
			// this is a copy from the class ClientRequest
			//
			SimpleDateFormat sdf = new SimpleDateFormat( PreferenceProperties.JMS_DATE_FORMAT);
	        java.util.Date currentDate = new java.util.Date();
	        String eventTime = sdf.format(currentDate);
	        message.setString( "EVENTTIME", eventTime);
			
		}
	    catch(JMSException jmse)
	    {
	    	CentralLogger.getInstance().debug(this,"IocChangeState : prepareJmsMessage : *** EXCEPTION *** : " + jmse.getMessage());
	    }  
		return message;
	}
}
