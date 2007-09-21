package org.csstudio.diag.interconnectionServer.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String XMPP_USER_NAME = "xmppUserName";
	public static final String XMPP_PASSWORD = "xmppPassword";
	public static final String DATA_PORT_NUMBER = "dataPortNumber";
	public static final String COMMAND_PORT_NUMBER = "commandPortNumber";
	public static final String SENT_START_ID = "sentStartID";
	public static final String JMS_CONTEXT_FACTORY = "jmsContextFactory";
	public static final String JMS_TIME_TO_LIVE_ALARMS = "jmsTimeToLiveAlarms";
	public static final String JMS_TIME_TO_LIVE_LOGS = "jmsTimeToLiveLogs";
	public static final String JMS_TIME_TO_LIVE_PUT_LOGS = "jmsTimeToLivePutLogs";
	public static final String PRIMARY_JMS_URL = "failover:(tcp://krynfs.desy.de:62616,tcp://krykjmsb.desy.de:64616)?maxReconnectDelay=500,maxReconnectAttempts=50";
	public static final String SECONDARY_JMS_URL = "failover:(tcp://krykjmsb.desy.de:64616,tcp://krynfs.desy.de:62616)?maxReconnectDelay=500,maxReconnectAttempts=50";

	
}
