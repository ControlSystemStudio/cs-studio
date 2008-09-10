package org.csstudio.nams.service.preferenceservice.declaration;

public enum PreferenceServiceJMSKeys implements HoldsAPreferenceId {
	/**
	 * External connection factory (JNDI id)
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsExternConnectionFactory",
			"External connection factory (JNDI id)"),

	/**
	 * External connection factory class.
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsExternConnectionFactoryClass",
			"External connection factory class"),
	/**
	 * .
	 */
	P_JMS_EXTERN_SENDER_PROVIDER_URL(
			"org.csstudio.ams.preferences.jmsExternSenderProviderUrl", "External sender URL"),
	/**
	 * External consumer URL #1.
	 */
	P_JMS_EXTERN_PROVIDER_URL_1(
			"org.csstudio.ams.preferences.jmsExternProviderUrl1", "External consumer URL #1"),

	/**
	 * External consumer URL #2
	 */
	P_JMS_EXTERN_PROVIDER_URL_2(
			"org.csstudio.ams.preferences.jmsExternProviderUrl2", "External consumer URL #2"),

	/**
	 * XXX What is it? Who use it? What the hell it does here? ;)
	 */
	P_JMS_EXT_TOPIC_STATUSCHANGE(
			"org.csstudio.ams.preferences.jmsExternTopicStatusChange",
			"Status change topic (?)"),

	/**
	 * Alarm source topic.
	 */
	P_JMS_EXT_TOPIC_ALARM("org.csstudio.ams.preferences.jmsExternTopicAlarm",
			"Alarm source topic"),

	/**
	 * Alarm source topic subscriber.
	 */
	P_JMS_EXT_TSUB_ALARM("org.csstudio.ams.preferences.jmsExternTSubAlarmFmr",
			"Alarm source topics subscriber"),

	/**
	 * External command topic.
	 */
	P_JMS_EXT_TOPIC_COMMAND(
			"org.csstudio.ams.preferences.jmsExternTopicCommand",
			"External command topic"),

	/**
	 * External command topics subscriber.
	 */
	P_JMS_EXT_TSUB_COMMAND(
			"org.csstudio.ams.preferences.jmsExternTSubCmdFmrStartReload",
			"External command topics subscriber"),

	/**
	 * SMS connectors inbox topic
	 */
	P_JMS_AMS_TOPIC_SMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicSmsConnector",
			"SMS connectors inbox topic"),

	/**
	 * SMS connectors inbox topics sub
	 */
	P_JMS_AMS_TSUB_SMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubSmsConnector",
			"SMS connectors inbox topics subscriber"),

	/**
	 * JMS connectors inbox topic.
	 */
	P_JMS_AMS_TOPIC_JMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicJMSConnector",
			"JMS connectors inbox topic"),

	/**
	 * JMS connectors inbox topic subscriber.
	 */
	P_JMS_AMS_TSUB_JMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubJMSConnector",
			"JMS connectors inbox topic subscriber"),

	/**
	 * Email connectors inbox topic
	 */
	P_JMS_AMS_TOPIC_EMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicEMailConnector",
			"Email connectors inbox topic"),

	/**
	 * Email connectors inbox topic sub
	 */
	P_JMS_AMS_TSUB_EMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubEMailConnector",
			"Email connectors inbox topic subscriber"),

	/**
	 * Voice-mail connectors inbox topic.
	 */
	P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicVoiceMailConnector",
			"Voice-mail connectors inbox topic"),

	/**
	 * Voice-mail connectors inbox topic subscriber.
	 */
	P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubVoiceMailConnector",
			"Voice-mail connectors inbox topic subscriber"),

	/**
	 * Inbox of distributor.
	 */
	P_JMS_AMS_TOPIC_DISTRIBUTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicDistributor",
			"Distributors inbox-topic"),

	/**
	 * Inbox of distributor.
	 */
	P_JMS_AMS_TSUB_DISTRIBUTOR(
			"org.csstudio.ams.preferences.jmsAmsTSubDistributor",
			"Distributors inbox-topic subscriber"),

	/**
	 * Reply topic XXX What is it?
	 */
	P_JMS_AMS_TOPIC_REPLY("org.csstudio.ams.preferences.jmsAmsTopicReply",
			"reply topic"),

	/**
	 * Reply topics sub.
	 */
	P_JMS_AMS_TSUB_REPLY("org.csstudio.ams.preferences.jmsAmsTSubReply",
			"reply topics subscriber"),

	/**
	 * Outbox des decission department (normally message minders inbox).
	 */
	P_JMS_AMS_TOPIC_DD_OUTBOX(
			"org.csstudio.ams.preferences.jmsAmsTopicMessageMinder",
			"Outbox-Topic of DD (normally message minders inbox)"),

	/**
	 * Subscriber of DDs outbox topic.
	 */
	P_JMS_AMS_TSUB_DD_OUTBOX(
			"org.csstudio.ams.preferences.jmsAmsTSubMessageMinder",
			"Subscriber of DDs outbox topic"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_COMMAND("org.csstudio.ams.preferences.jmsAmsTopicCommand",
			"Command exchange topic"),

	/**
	 * XXX ?
	 */
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsFreeTopicConnectionFactoryClass"),

	/**
	 * XXX ?
	 */
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsFreeTopicConnectionFactory"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsAmsConnectionFactory",
			"Interne connection factory (JNDI id)"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsExternConnectionFactoryClass",
			"Interne connection factory class"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_1("org.csstudio.ams.preferences.jmsAmsProviderUrl1",
			"Interner consumer provider URL #1"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_2("org.csstudio.ams.preferences.jmsAmsProviderUrl2",
			"Interner consumer provider URL #2"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_SENDER_PROVIDER_URL(
			"org.csstudio.ams.preferences.jmsAmsSenderProviderUrl",
			"Sender provider URL"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_COMMAND_DECISSION_DEPARTMENT_SUBSCRIBER_ID(
			"org.csstudio.ams.preferences.jmsAmsTSubCmdFmrReloadEnd",
			"Command exchange topic subscriber ID of decission department (DD)");

	private String _key;
	private String _description;

	private PreferenceServiceJMSKeys(final String key) {
		this(key, "No description available");
	}

	private PreferenceServiceJMSKeys(final String key, final String description) {
		this._key = key;
		this._description = description;
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
