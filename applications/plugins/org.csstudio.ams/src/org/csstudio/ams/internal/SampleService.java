/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.ams.internal;

import org.eclipse.jface.preference.IPreferenceStore;
import org.csstudio.ams.Activator;
import org.csstudio.ams.Log;

/**
 * This example service demonstrates that the preference IDs that are used on
 * preference pages should be defined by the services that use them.<br>
 * </p>
 * Beside the definition of the preference IDs, this services provides the
 * method <code>showPreferences</code> that reads out the preference values
 * from the plugin's preference store and displays them. This demonstrates how
 * the preferences are accessed.
 * 
 * @author Alexander Will
 * 
 */
public class SampleService
{
	// database settings
	public static final String P_CONFIG_DATABASE_CONNECTION = "org.csstudio.ams.preferences.configDatabaseConnection";
	public static final String P_CONFIG_DATABASE_USER = "org.csstudio.ams.preferences.configDatabaseUser";
	public static final String P_CONFIG_DATABASE_PASSWORD = "org.csstudio.ams.preferences.configDatabasePassword";
	public static final String P_APP_DATABASE_CONNECTION = "org.csstudio.ams.preferences.appDatabaseConnection";
	public static final String P_APP_DATABASE_USER = "org.csstudio.ams.preferences.appDatabaseUser";
	public static final String P_APP_DATABASE_PASSWORD = "org.csstudio.ams.preferences.appDatabasePassword";

	// filter key field of message
	public static final String P_FILTER_KEYFIELDS = "org.csstudio.ams.preferences.filterKeyFields";

	// jms communication - external
	public static final String P_JMS_EXTERN_CONNECTION_FACTORY_CLASS = "org.csstudio.ams.preferences.jmsExternConnectionFactoryClass";
	public static final String P_JMS_EXTERN_PROVIDER_URL_1 = "org.csstudio.ams.preferences.jmsExternProviderUrl1";
    public static final String P_JMS_EXTERN_PROVIDER_URL_2 = "org.csstudio.ams.preferences.jmsExternProviderUrl2";
    public static final String P_JMS_EXTERN_SENDER_PROVIDER_URL = "org.csstudio.ams.preferences.jmsExternSenderProviderUrl";
	public static final String P_JMS_EXTERN_CONNECTION_FACTORY = "org.csstudio.ams.preferences.jmsExternConnectionFactory";
	public static final String P_JMS_EXTERN_CREATE_DURABLE = "org.csstudio.ams.preferences.jmsExternCreateDurable";
	
	// jms communication - ams internal
	public static final String P_JMS_AMS_CONNECTION_FACTORY_CLASS = "org.csstudio.ams.preferences.jmsAmsConnectionFactoryClass";
	public static final String P_JMS_AMS_PROVIDER_URL_1 = "org.csstudio.ams.preferences.jmsAmsProviderUrl1";
    public static final String P_JMS_AMS_PROVIDER_URL_2 = "org.csstudio.ams.preferences.jmsAmsProviderUrl2";
    public static final String P_JMS_AMS_SENDER_PROVIDER_URL = "org.csstudio.ams.preferences.jmsAmsSenderProviderUrl";
	public static final String P_JMS_AMS_CONNECTION_FACTORY = "org.csstudio.ams.preferences.jmsAmsConnectionFactory";
    public static final String P_JMS_AMS_CREATE_DURABLE = "org.csstudio.ams.preferences.jmsAmsCreateDurable";
	
	// jms communication - free topics
	public static final String P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS = "org.csstudio.ams.preferences.jmsFreeTopicConnectionFactoryClass";
	public static final String P_JMS_FREE_TOPIC_CONNECTION_FACTORY = "org.csstudio.ams.preferences.jmsFreeTopicConnectionFactory";

	// external topics
	public static final String P_JMS_EXT_TOPIC_ALARM = "org.csstudio.ams.preferences.jmsExternTopicAlarm";
	public static final String P_JMS_EXT_TSUB_ALARM_FMR = "org.csstudio.ams.preferences.jmsExternTSubAlarmFmr";
	public static final String P_JMS_EXT_TOPIC_COMMAND = "org.csstudio.ams.preferences.jmsExternTopicCommand";
	public static final String P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD = "org.csstudio.ams.preferences.jmsExternTSubCmdFmrStartReload";
	public static final String P_JMS_EXT_TOPIC_STATUSCHANGE = "org.csstudio.ams.preferences.jmsExternTopicStatusChange";

	// ams internal topics
	public static final String P_JMS_AMS_TOPIC_DISTRIBUTOR = "org.csstudio.ams.preferences.jmsAmsTopicDistributor";
	public static final String P_JMS_AMS_TSUB_DISTRIBUTOR = "org.csstudio.ams.preferences.jmsAmsTSubDistributor";
    public static final String P_JMS_AMS_TOPIC_MESSAGEMINDER = "org.csstudio.ams.preferences.jmsAmsTopicMessageMinder";
    public static final String P_JMS_AMS_TSUB_MESSAGEMINDER = "org.csstudio.ams.preferences.jmsAmsTSubMessageMinder";
	public static final String P_JMS_AMS_TOPIC_REPLY = "org.csstudio.ams.preferences.jmsAmsTopicReply";
	public static final String P_JMS_AMS_TSUB_REPLY = "org.csstudio.ams.preferences.jmsAmsTSubReply";

	public static final String P_JMS_AMS_TOPIC_SMS_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTopicSmsConnector";
    public static final String P_JMS_AMS_TOPIC_SMS_CONNECTOR_FORWARD = "org.csstudio.ams.preferences.jmsAmsTopicSmsConnectorForward";
	public static final String P_JMS_AMS_TSUB_SMS_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTSubSmsConnector";
	public static final String P_JMS_AMS_TOPIC_EMAIL_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTopicEMailConnector";
    public static final String P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_FORWARD = "org.csstudio.ams.preferences.jmsAmsTopicEMailConnectorForward";
	public static final String P_JMS_AMS_TSUB_EMAIL_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTSubEMailConnector";
	public static final String P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTopicVoiceMailConnector";
    public static final String P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_FORWARD = "org.csstudio.ams.preferences.jmsAmsTopicVoiceMailConnectorForward";
	public static final String P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTSubVoiceMailConnector";
	public static final String P_JMS_AMS_TOPIC_JMS_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTopicJMSConnector";
    public static final String P_JMS_AMS_TOPIC_JMS_CONNECTOR_FORWARD = "org.csstudio.ams.preferences.jmsAmsTopicJMSConnectorForward";
	public static final String P_JMS_AMS_TSUB_JMS_CONNECTOR = "org.csstudio.ams.preferences.jmsAmsTSubJMSConnector";

	public static final String P_JMS_AMS_TOPIC_COMMAND = "org.csstudio.ams.preferences.jmsAmsTopicCommand";
	public static final String P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END = "org.csstudio.ams.preferences.jmsAmsTSubCmdFmrReloadEnd";	

	public static final String P_JMS_AMS_TOPIC_MONITOR = "org.csstudio.ams.preferences.jmsAmsTopicMonitor";

	/**
	 * The only one instance of this service.
	 */
	private static SampleService _instance;

	/**
	 * Private constructor due to the singleton pattern.
	 */
	private SampleService() {
		// do nothing particular.
	}

	/**
	 * Return the only one instance of this service.
	 * 
	 * @return The only one instance of this service.
	 */
	public static SampleService getInstance() {
		if (_instance == null) {
			_instance = new SampleService();
		}

		return _instance;
	}

	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 * 
	 */
	public final void showPreferences() 
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		// database settings
		Log.log(Log.INFO, P_CONFIG_DATABASE_CONNECTION + ": " + store.getString(P_CONFIG_DATABASE_CONNECTION));
		Log.log(Log.INFO, P_CONFIG_DATABASE_USER + ": " + store.getString(P_CONFIG_DATABASE_USER));
		Log.log(Log.INFO, P_CONFIG_DATABASE_PASSWORD + ": " + store.getString(P_CONFIG_DATABASE_PASSWORD));
		Log.log(Log.INFO, P_APP_DATABASE_CONNECTION + ": " + store.getString(P_APP_DATABASE_CONNECTION));
		Log.log(Log.INFO, P_APP_DATABASE_USER + ": " + store.getString(P_APP_DATABASE_USER));
		Log.log(Log.INFO, P_APP_DATABASE_PASSWORD + ": " + store.getString(P_APP_DATABASE_PASSWORD));

		// filter key field of message
		String[] columnNames = store.getString(P_FILTER_KEYFIELDS).split(";");
		Log.log(Log.INFO, P_FILTER_KEYFIELDS + ": " + columnNames);

		// jms communication - external
		Log.log(Log.INFO, P_JMS_EXTERN_CONNECTION_FACTORY_CLASS + ": " + store.getString(P_JMS_EXTERN_CONNECTION_FACTORY_CLASS));
		Log.log(Log.INFO, P_JMS_EXTERN_PROVIDER_URL_1 + ": " + store.getString(P_JMS_EXTERN_PROVIDER_URL_1));
        Log.log(Log.INFO, P_JMS_EXTERN_PROVIDER_URL_2 + ": " + store.getString(P_JMS_EXTERN_PROVIDER_URL_2));
		Log.log(Log.INFO, P_JMS_EXTERN_CONNECTION_FACTORY + ": " + store.getString(P_JMS_EXTERN_CONNECTION_FACTORY));
        Log.log(Log.INFO, P_JMS_EXTERN_CREATE_DURABLE + ": " + store.getString(P_JMS_EXTERN_CREATE_DURABLE));
		
		// jms communication - ams internal
		Log.log(Log.INFO, P_JMS_AMS_CONNECTION_FACTORY_CLASS + ": " + store.getString(P_JMS_AMS_CONNECTION_FACTORY_CLASS));
		Log.log(Log.INFO, P_JMS_AMS_PROVIDER_URL_1 + ": " + store.getString(P_JMS_AMS_PROVIDER_URL_1));
        Log.log(Log.INFO, P_JMS_AMS_PROVIDER_URL_2 + ": " + store.getString(P_JMS_AMS_PROVIDER_URL_2));
		Log.log(Log.INFO, P_JMS_AMS_CONNECTION_FACTORY + ": " + store.getString(P_JMS_AMS_CONNECTION_FACTORY));
        Log.log(Log.INFO, P_JMS_AMS_CREATE_DURABLE + ": " + store.getString(P_JMS_AMS_CREATE_DURABLE));
		
		// jms communication - free topics
		Log.log(Log.INFO, P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS + ": " + store.getString(P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS));
		Log.log(Log.INFO, P_JMS_FREE_TOPIC_CONNECTION_FACTORY + ": " + store.getString(P_JMS_FREE_TOPIC_CONNECTION_FACTORY));
		
		// external topics
		Log.log(Log.INFO, P_JMS_EXT_TOPIC_ALARM + ": " + store.getString(P_JMS_EXT_TOPIC_ALARM));
		Log.log(Log.INFO, P_JMS_EXT_TSUB_ALARM_FMR + ": " + store.getString(P_JMS_EXT_TSUB_ALARM_FMR));
		Log.log(Log.INFO, P_JMS_EXT_TOPIC_COMMAND + ": " + store.getString(P_JMS_EXT_TOPIC_COMMAND));
		Log.log(Log.INFO, P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD + ": " + store.getString(P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD));
		Log.log(Log.INFO, P_JMS_EXT_TOPIC_STATUSCHANGE + ": " + store.getString(P_JMS_EXT_TOPIC_STATUSCHANGE));
				
		// ams internal topics
		Log.log(Log.INFO, P_JMS_AMS_TOPIC_DISTRIBUTOR + ": " + store.getString(P_JMS_AMS_TOPIC_DISTRIBUTOR));
		Log.log(Log.INFO, P_JMS_AMS_TSUB_DISTRIBUTOR + ": " + store.getString(P_JMS_AMS_TSUB_DISTRIBUTOR));
		Log.log(Log.INFO, P_JMS_AMS_TOPIC_REPLY + ": " + store.getString(P_JMS_AMS_TOPIC_REPLY));
		Log.log(Log.INFO, P_JMS_AMS_TSUB_REPLY + ": " + store.getString(P_JMS_AMS_TSUB_REPLY));

		Log.log(Log.INFO, P_JMS_AMS_TOPIC_SMS_CONNECTOR + ": " + store.getString(P_JMS_AMS_TOPIC_SMS_CONNECTOR));
		Log.log(Log.INFO, P_JMS_AMS_TSUB_SMS_CONNECTOR + ": " + store.getString(P_JMS_AMS_TSUB_SMS_CONNECTOR));
		Log.log(Log.INFO, P_JMS_AMS_TOPIC_EMAIL_CONNECTOR + ": " + store.getString(P_JMS_AMS_TOPIC_EMAIL_CONNECTOR));
		Log.log(Log.INFO, P_JMS_AMS_TSUB_EMAIL_CONNECTOR + ": " + store.getString(P_JMS_AMS_TSUB_EMAIL_CONNECTOR));
		Log.log(Log.INFO, P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR + ": " + store.getString(P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR));
		Log.log(Log.INFO, P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR + ": " + store.getString(P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR));

		Log.log(Log.INFO, P_JMS_AMS_TOPIC_COMMAND + ": " + store.getString(P_JMS_AMS_TOPIC_COMMAND));
		Log.log(Log.INFO, P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END + ": " + store.getString(P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END));
	}
}