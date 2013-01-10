
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

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.eclipse.jface.preference.IPreferenceStore;

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
public class AmsPreferenceKey
{
	// Configuration database settings
    public static final String P_CONFIG_DATABASE_TYPE = "configDatabaseType";
	public static final String P_CONFIG_DATABASE_CONNECTION = "configDatabaseConnection";
	public static final String P_CONFIG_DATABASE_USER = "configDatabaseUser";
	public static final String P_CONFIG_DATABASE_PASSWORD = "configDatabasePassword";

    // Application database settings
	public static final String P_APP_DATABASE_TYPE = "appDatabaseType";
	public static final String P_APP_DATABASE_CONNECTION = "appDatabaseConnection";
	public static final String P_APP_DATABASE_USER = "appDatabaseUser";
	public static final String P_APP_DATABASE_PASSWORD = "appDatabasePassword";

    // Memory cache database settings
    public static final String P_CACHE_DATABASE_TYPE = "cacheDatabaseType";
    public static final String P_CACHE_DATABASE_CONNECTION = "cacheDatabaseConnection";
    public static final String P_CACHE_DATABASE_USER = "cacheDatabaseUser";
    public static final String P_CACHE_DATABASE_PASSWORD = "cacheDatabasePassword";

    // filter key field of message
	public static final String P_FILTER_KEYFIELDS = "filterKeyFields";

	// AMS management password
	public static final String P_AMS_MANAGEMENT_PASSWORD = "managementPassword";

	// jms communication - external
	public static final String P_JMS_EXTERN_CONNECTION_FACTORY_CLASS = "jmsExternConnectionFactoryClass";
	public static final String P_JMS_EXTERN_PROVIDER_URL_1 = "jmsExternProviderUrl1";
    public static final String P_JMS_EXTERN_PROVIDER_URL_2 = "jmsExternProviderUrl2";
    public static final String P_JMS_EXTERN_SENDER_PROVIDER_URL = "jmsExternSenderProviderUrl";
	public static final String P_JMS_EXTERN_CONNECTION_FACTORY = "jmsExternConnectionFactory";
	public static final String P_JMS_EXTERN_CREATE_DURABLE = "jmsExternCreateDurable";

	// jms communication - ams internal
	public static final String P_JMS_AMS_CONNECTION_FACTORY_CLASS = "jmsAmsConnectionFactoryClass";
	public static final String P_JMS_AMS_PROVIDER_URL_1 = "jmsAmsProviderUrl1";
    public static final String P_JMS_AMS_PROVIDER_URL_2 = "jmsAmsProviderUrl2";
    public static final String P_JMS_AMS_SENDER_PROVIDER_URL = "jmsAmsSenderProviderUrl";
	public static final String P_JMS_AMS_CONNECTION_FACTORY = "jmsAmsConnectionFactory";
    public static final String P_JMS_AMS_CREATE_DURABLE = "jmsAmsCreateDurable";

	// jms communication - free topics
	public static final String P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS = "jmsFreeTopicConnectionFactoryClass";
	public static final String P_JMS_FREE_TOPIC_CONNECTION_FACTORY = "jmsFreeTopicConnectionFactory";

	// external topics
	public static final String P_JMS_EXT_TOPIC_ALARM = "jmsExternTopicAlarm";
    public static final String P_JMS_EXT_TOPIC_ALARM_REINSERT = "jmsExternTopicAlarmReInsert";
	public static final String P_JMS_EXT_TSUB_ALARM_FMR = "jmsExternTSubAlarmFmr";
	public static final String P_JMS_EXT_TOPIC_COMMAND = "jmsExternTopicCommand";
	public static final String P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD = "jmsExternTSubCmdFmrStartReload";
	public static final String P_JMS_EXT_TOPIC_STATUSCHANGE = "jmsExternTopicStatusChange";

	// ams internal topics
	public static final String P_JMS_AMS_TOPIC_DISTRIBUTOR = "jmsAmsTopicDistributor";
	public static final String P_JMS_AMS_TSUB_DISTRIBUTOR = "jmsAmsTSubDistributor";
    public static final String P_JMS_AMS_TOPIC_MESSAGEMINDER = "jmsAmsTopicMessageMinder";
    public static final String P_JMS_AMS_TSUB_MESSAGEMINDER = "jmsAmsTSubMessageMinder";
	public static final String P_JMS_AMS_TOPIC_REPLY = "jmsAmsTopicReply";
	public static final String P_JMS_AMS_TSUB_REPLY = "jmsAmsTSubReply";

	public static final String P_JMS_AMS_TOPIC_CONNECTOR_DEVICETEST = "jmsAmsTopicConnectorDeviceTest";
	public static final String P_JMS_AMS_TOPIC_SMS_CONNECTOR = "jmsAmsTopicSmsConnector";
    public static final String P_JMS_AMS_TOPIC_SMS_CONNECTOR_FORWARD = "jmsAmsTopicSmsConnectorForward";
	public static final String P_JMS_AMS_TSUB_SMS_CONNECTOR = "jmsAmsTSubSmsConnector";
	public static final String P_JMS_AMS_TSUB_SMS_CONNECTOR_DEVICETEST = "jmsAmsTSubSmsConnectorDeviceTest";
    public static final String P_JMS_AMS_TOPIC_EMAIL_CONNECTOR = "jmsAmsTopicEMailConnector";
    public static final String P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_FORWARD = "jmsAmsTopicEMailConnectorForward";
	public static final String P_JMS_AMS_TSUB_EMAIL_CONNECTOR = "jmsAmsTSubEMailConnector";
	public static final String P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR = "jmsAmsTopicVoiceMailConnector";
    public static final String P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_FORWARD = "jmsAmsTopicVoiceMailConnectorForward";
	public static final String P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR = "jmsAmsTSubVoiceMailConnector";
	public static final String P_JMS_AMS_TOPIC_JMS_CONNECTOR = "jmsAmsTopicJMSConnector";
    public static final String P_JMS_AMS_TOPIC_JMS_CONNECTOR_FORWARD = "jmsAmsTopicJMSConnectorForward";
	public static final String P_JMS_AMS_TSUB_JMS_CONNECTOR = "jmsAmsTSubJMSConnector";

	public static final String P_JMS_AMS_TOPIC_COMMAND = "jmsAmsTopicCommand";
	public static final String P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END = "jmsAmsTSubCmdFmrReloadEnd";

	public static final String P_JMS_AMS_TOPIC_MONITOR = "jmsAmsTopicMonitor";

	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 *
	 */
	public static final void showPreferences()
	{
		final IPreferenceStore store = AmsActivator.getDefault().getPreferenceStore();

		// database settings
		Log.log(Log.INFO, P_CONFIG_DATABASE_CONNECTION + ": " + store.getString(P_CONFIG_DATABASE_CONNECTION));
		Log.log(Log.INFO, P_CONFIG_DATABASE_USER + ": " + store.getString(P_CONFIG_DATABASE_USER));
		Log.log(Log.INFO, P_CONFIG_DATABASE_PASSWORD + ": " + store.getString(P_CONFIG_DATABASE_PASSWORD));
		Log.log(Log.INFO, P_APP_DATABASE_CONNECTION + ": " + store.getString(P_APP_DATABASE_CONNECTION));
		Log.log(Log.INFO, P_APP_DATABASE_USER + ": " + store.getString(P_APP_DATABASE_USER));
		Log.log(Log.INFO, P_APP_DATABASE_PASSWORD + ": " + store.getString(P_APP_DATABASE_PASSWORD));

		// filter key field of message
		final String[] columnNames = store.getString(P_FILTER_KEYFIELDS).split(";");
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