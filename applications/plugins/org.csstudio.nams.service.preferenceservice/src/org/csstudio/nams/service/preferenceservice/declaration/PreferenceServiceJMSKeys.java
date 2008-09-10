package org.csstudio.nams.service.preferenceservice.declaration;

import org.csstudio.nams.service.preferenceservice.Messages;

public enum PreferenceServiceJMSKeys implements HoldsAPreferenceId {
	/**
	 * External connection factory (JNDI id)
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsExternConnectionFactory", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_CONNECTION_FACTORY_label),

	/**
	 * External connection factory class.
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsExternConnectionFactoryClass", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_CONNECTION_FACTORY_CLASS_label),
	/**
	 * External sender URL.
	 */
	P_JMS_EXTERN_SENDER_PROVIDER_URL(
			"org.csstudio.ams.preferences.jmsExternSenderProviderUrl", Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_SENDER_PROVIDER_URL_label), //$NON-NLS-1$
	/**
	 * External consumer URL #1.
	 */
	P_JMS_EXTERN_PROVIDER_URL_1(
			"org.csstudio.ams.preferences.jmsExternProviderUrl1", Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_PROVIDER_URL_1_label), //$NON-NLS-1$

	/**
	 * External consumer URL #2
	 */
	P_JMS_EXTERN_PROVIDER_URL_2(
			"org.csstudio.ams.preferences.jmsExternProviderUrl2", Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_PROVIDER_URL_2_label), //$NON-NLS-1$

	/**
	 * XXX What is it? Who use it? What the hell it does here? ;)
	 */
	P_JMS_EXT_TOPIC_STATUSCHANGE(
			"org.csstudio.ams.preferences.jmsExternTopicStatusChange", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_STATUSCHANGE_label),

	/**
	 * Alarm source topic.
	 */
	P_JMS_EXT_TOPIC_ALARM("org.csstudio.ams.preferences.jmsExternTopicAlarm", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_ALARM_label),

	/**
	 * Alarm source topic subscriber.
	 */
	P_JMS_EXT_TSUB_ALARM("org.csstudio.ams.preferences.jmsExternTSubAlarmFmr", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TSUB_ALARM_label),

	/**
	 * External command topic.
	 */
	P_JMS_EXT_TOPIC_COMMAND(
			"org.csstudio.ams.preferences.jmsExternTopicCommand", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_COMMAND_label),

	/**
	 * External command topics subscriber.
	 */
	P_JMS_EXT_TSUB_COMMAND(
			"org.csstudio.ams.preferences.jmsExternTSubCmdFmrStartReload", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TSUB_COMMAND_label),

	/**
	 * SMS connectors inbox topic
	 */
	P_JMS_AMS_TOPIC_SMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicSmsConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_SMS_CONNECTOR_label),

	/**
	 * SMS connectors inbox topics sub
	 */
	P_JMS_AMS_TSUB_SMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubSmsConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_SMS_CONNECTOR_label),

	/**
	 * JMS connectors inbox topic.
	 */
	P_JMS_AMS_TOPIC_JMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicJMSConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_JMS_CONNECTOR_label),

	/**
	 * JMS connectors inbox topic subscriber.
	 */
	P_JMS_AMS_TSUB_JMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubJMSConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_JMS_CONNECTOR_label),

	/**
	 * Email connectors inbox topic
	 */
	P_JMS_AMS_TOPIC_EMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicEMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_label),

	/**
	 * Email connectors inbox topic sub
	 */
	P_JMS_AMS_TSUB_EMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubEMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_EMAIL_CONNECTOR_label),

	/**
	 * Voice-mail connectors inbox topic.
	 */
	P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicVoiceMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_label),

	/**
	 * Voice-mail connectors inbox topic subscriber.
	 */
	P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubVoiceMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR_label),

	/**
	 * Inbox of distributor.
	 */
	P_JMS_AMS_TOPIC_DISTRIBUTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicDistributor", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_DISTRIBUTOR_label),

	/**
	 * Inbox of distributor.
	 */
	P_JMS_AMS_TSUB_DISTRIBUTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubDistributor", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_DISTRIBUTOR_label),

	/**
	 * Reply topic XXX What is it?
	 */
	P_JMS_AMS_TOPIC_REPLY("org.csstudio.ams.preferences.jmsAmsTopicReply", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_REPLY_label),

	/**
	 * Reply topics sub.
	 */
	P_JMS_AMS_TSUB_REPLY("org.csstudio.ams.preferences.jmsAmsTSubReply", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_REPLY_label),

	/**
	 * Outbox des decission department (normally message minders inbox).
	 */
	P_JMS_AMS_TOPIC_DD_OUTBOX(
			"org.csstudio.ams.preferences.jmsAmsTopicMessageMinder", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_DD_OUTBOX_label),

	/**
	 * Subscriber of DDs outbox topic.
	 */
	P_JMS_AMS_TSUB_DD_OUTBOX(
			"org.csstudio.ams.preferences.jmsAmsTSubMessageMinder", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_DD_OUTBOX_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_COMMAND("org.csstudio.ams.preferences.jmsAmsTopicCommand", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_COMMAND_label),

	/**
	 * XXX ?
	 */
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsFreeTopicConnectionFactoryClass"), //$NON-NLS-1$

	/**
	 * XXX ?
	 */
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsFreeTopicConnectionFactory"), //$NON-NLS-1$

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsAmsConnectionFactory", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_CONNECTION_FACTORY_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsExternConnectionFactoryClass", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_CONNECTION_FACTORY_CLASS_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_1("org.csstudio.ams.preferences.jmsAmsProviderUrl1", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_PROVIDER_URL_1_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_2("org.csstudio.ams.preferences.jmsAmsProviderUrl2", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_PROVIDER_URL_2_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_SENDER_PROVIDER_URL(
			"org.csstudio.ams.preferences.jmsAmsSenderProviderUrl", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_SENDER_PROVIDER_URL_label),

	/**
	 * Topic command sub.
	 */
	P_JMS_AMS_TSUB_COMMAND_DECISSION_DEPARTMENT(
			"org.csstudio.ams.preferences.jmsAmsTSubCmdFmrReloadEnd", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_COMMAND_DECISSION_DEPARTMENT_label),

	/**
	 * 
	 */
	P_JMS_EXT_SYNCHRONIZE_PRODUCER_ID(
			"org.csstudio.ams.preferences.jmsExtSynchronizeProducerId", //$NON-NLS-1$
			"Configurator Synchronize producer Id"),
			
	/**
	 * 
	 */
	P_JMS_EXT_SYNCHRONIZE_CONSUMER_ID(
			"org.csstudio.ams.preferences.jmsExtSynchronizeConsumerId", //$NON-NLS-1$
			"Configurator Synchronize consumer Id");

	private String _key;
	private String _description;

	private PreferenceServiceJMSKeys(final String key) {
		this(
				key,
				Messages.PreferenceServiceJMSKeys_fallback_message_on_missing_label);
	}

	private PreferenceServiceJMSKeys(final String key, final String description) {
		this._key = key;
		this._description = ((description != null && description.length() > 0) ? description
				: Messages.PreferenceServiceJMSKeys_fallback_message_on_missing_label);
	}

	public String getDescription() {
		return this._description;
	}

	public String getKey() {
		return this._key;
	}

	public String getPreferenceStoreId() {
		return this._key;
	}

}
