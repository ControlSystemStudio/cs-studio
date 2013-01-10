
package org.csstudio.nams.service.preferenceservice.declaration;

import org.csstudio.nams.service.preferenceservice.Messages;

public enum PreferenceServiceJMSKeys implements HoldsAPreferenceId
{
	/**
	 * External connection factory (JNDI id)
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY(
			"jmsExternConnectionFactory", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_CONNECTION_FACTORY_label),

	/**
	 * External connection factory class.
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY_CLASS(
			"jmsExternConnectionFactoryClass", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_CONNECTION_FACTORY_CLASS_label),
	/**
	 * External sender URL.
	 */
	P_JMS_EXTERN_SENDER_PROVIDER_URL(
			"jmsExternSenderProviderUrl", Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_SENDER_PROVIDER_URL_label), //$NON-NLS-1$
	/**
	 * External consumer URL #1.
	 */
	P_JMS_EXTERN_PROVIDER_URL_1(
			"jmsExternProviderUrl1", Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_PROVIDER_URL_1_label), //$NON-NLS-1$

	/**
	 * External consumer URL #2
	 */
	P_JMS_EXTERN_PROVIDER_URL_2(
			"jmsExternProviderUrl2", Messages.PreferenceServiceJMSKeys_P_JMS_EXTERN_PROVIDER_URL_2_label), //$NON-NLS-1$

	/**
	 * XXX What is it? Who use it? What the hell it does here? ;)
	 */
	P_JMS_EXT_TOPIC_STATUSCHANGE(
			"jmsExternTopicStatusChange", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_STATUSCHANGE_label),

	/**
	 * Alarm source topic.
	 */
	P_JMS_EXT_TOPIC_ALARM("jmsExternTopicAlarm", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_ALARM_label),

    /**
     * Alarm source topic for re-insert.
     */
    P_JMS_EXT_TOPIC_ALARM_REINSERT("jmsExternTopicAlarmReInsert", //$NON-NLS-1$
            Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_ALARM_label),

		            /**
	 * Alarm source topic subscriber.
	 */
	P_JMS_EXT_TSUB_ALARM("jmsExternTSubAlarmFmr", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TSUB_ALARM_label),

	/**
	 * External command topic.
	 */
	P_JMS_EXT_TOPIC_COMMAND(
			"jmsExternTopicCommand", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TOPIC_COMMAND_label),

	/**
	 * External command topics subscriber.
	 */
	P_JMS_EXT_TSUB_COMMAND(
			"jmsExternTSubCmdFmrStartReload", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_EXT_TSUB_COMMAND_label),

	/**
	 * SMS connectors inbox topic
	 */
	P_JMS_AMS_TOPIC_SMS_CONNECTOR(
			"jmsAmsTopicSmsConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_SMS_CONNECTOR_label),

	/**
	 * SMS connectors inbox topics sub
	 */
	P_JMS_AMS_TSUB_SMS_CONNECTOR(
			"jmsAmsTSubSmsConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_SMS_CONNECTOR_label),

	/**
	 * JMS connectors inbox topic.
	 */
	P_JMS_AMS_TOPIC_JMS_CONNECTOR(
			"jmsAmsTopicJMSConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_JMS_CONNECTOR_label),

	/**
	 * JMS connectors inbox topic subscriber.
	 */
	P_JMS_AMS_TSUB_JMS_CONNECTOR(
			"jmsAmsTSubJMSConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_JMS_CONNECTOR_label),

	/**
	 * Email connectors inbox topic
	 */
	P_JMS_AMS_TOPIC_EMAIL_CONNECTOR(
			"jmsAmsTopicEMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_label),

	/**
	 * Email connectors inbox topic sub
	 */
	P_JMS_AMS_TSUB_EMAIL_CONNECTOR(
			"jmsAmsTSubEMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_EMAIL_CONNECTOR_label),

	/**
	 * Voice-mail connectors inbox topic.
	 */
	P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR(
			"jmsAmsTopicVoiceMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_label),

	/**
	 * Voice-mail connectors inbox topic subscriber.
	 */
	P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR(
			"jmsAmsTSubVoiceMailConnector", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR_label),

	/**
	 * Inbox of distributor.
	 */
	P_JMS_AMS_TOPIC_DISTRIBUTOR(
			"jmsAmsTopicDistributor", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_DISTRIBUTOR_label),

	/**
	 * Inbox of distributor.
	 */
	P_JMS_AMS_TSUB_DISTRIBUTOR(
			"jmsAmsTSubDistributor", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_DISTRIBUTOR_label),

	/**
	 * Reply topic XXX What is it?
	 */
	P_JMS_AMS_TOPIC_REPLY("jmsAmsTopicReply", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_REPLY_label),

	/**
	 * Reply topics sub.
	 */
	P_JMS_AMS_TSUB_REPLY("jmsAmsTSubReply", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_REPLY_label),

	/**
	 * Outbox des decission department (normally message minders inbox).
	 */
	P_JMS_AMS_TOPIC_DD_OUTBOX(
			"jmsAmsTopicMessageMinder", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_DD_OUTBOX_label),

	/**
	 * Subscriber of DDs outbox topic.
	 */
	P_JMS_AMS_TSUB_DD_OUTBOX(
			"jmsAmsTSubMessageMinder", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_DD_OUTBOX_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_COMMAND("jmsAmsTopicCommand", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TOPIC_COMMAND_label),

	/**
	 * XXX ?
	 */
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS(
			"jmsFreeTopicConnectionFactoryClass"), //$NON-NLS-1$

	/**
	 * XXX ?
	 */
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY(
			"jmsFreeTopicConnectionFactory"), //$NON-NLS-1$

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY(
			"jmsAmsConnectionFactory", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_CONNECTION_FACTORY_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY_CLASS(
			"jmsExternConnectionFactoryClass", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_CONNECTION_FACTORY_CLASS_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_1("jmsAmsProviderUrl1", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_PROVIDER_URL_1_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_2("jmsAmsProviderUrl2", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_PROVIDER_URL_2_label),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_SENDER_PROVIDER_URL(
			"jmsAmsSenderProviderUrl", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_SENDER_PROVIDER_URL_label),

	/**
	 * Topic command sub.
	 */
	P_JMS_AMS_TSUB_COMMAND_DECISSION_DEPARTMENT(
			"jmsAmsTSubCmdFmrReloadEnd", //$NON-NLS-1$
			Messages.PreferenceServiceJMSKeys_P_JMS_AMS_TSUB_COMMAND_DECISSION_DEPARTMENT_label),

	/**
	 * 
	 */
	P_JMS_EXT_SYNCHRONIZE_PRODUCER_ID(
			"jmsExtSynchronizeProducerId", //$NON-NLS-1$
			"Configurator Synchronize producer Id"),
			
	/**
	 * 
	 */
	P_JMS_EXT_SYNCHRONIZE_CONSUMER_ID(
			"jmsExtSynchronizeConsumerId", //$NON-NLS-1$
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

	@Override
    public String getDescription() {
		return this._description;
	}

	public String getKey() {
		return this._key;
	}

	@Override
    public String getPreferenceStoreId() {
		return this._key;
	}
}
