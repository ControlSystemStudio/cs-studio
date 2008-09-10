package org.csstudio.nams.service.preferenceservice.declaration;

public enum PreferenceServiceJMSKeys implements HoldsAPreferenceId {
	/**
	 * XXX ?
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY(
			"org.csstudio.ams.preferences.jmsExternConnectionFactory",
			"deprecated preference - backward compatibility (P_JMS_EXTERN_CONNECTION_FACTORY)"),

	/**
	 * XXX ?
	 */
	P_JMS_EXTERN_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsExternConnectionFactoryClass",
			"Internal connection factory class"),
	/**
	 * XXX ?
	 */
	P_JMS_EXTERN_SENDER_PROVIDER_URL(
			"org.csstudio.ams.preferences.jmsExternSenderProviderUrl"),
	/**
	 * XXX ?
	 */
	P_JMS_EXTERN_PROVIDER_URL_1(
			"org.csstudio.ams.preferences.jmsExternProviderUrl1"),

	/**
	 * XXX ?
	 */
	P_JMS_EXTERN_PROVIDER_URL_2(
			"org.csstudio.ams.preferences.jmsExternProviderUrl2"),

	/**
	 * XXX ?
	 */
	P_JMS_EXT_TOPIC_STATUSCHANGE(
			"org.csstudio.ams.preferences.jmsExternTopicStatusChange"),

	/**
	 * XXX ?
	 */
	P_JMS_EXT_TOPIC_ALARM("org.csstudio.ams.preferences.jmsExternTopicAlarm"),

	/**
	 * XXX ?
	 */
	P_JMS_EXT_TOPIC_COMMAND(
			"org.csstudio.ams.preferences.jmsExternTopicCommand"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_SMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicSmsConnector"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_JMS_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicJMSConnector"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_EMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicEMailConnector"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicVoiceMailConnector"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_DISTRIBUTOR(
			"org.csstudio.ams.preferences.jmsAmsTopicDistributor"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_REPLY("org.csstudio.ams.preferences.jmsAmsTopicReply"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_TOPIC_MESSAGEMINDER(
			"org.csstudio.ams.preferences.jmsAmsTopicMessageMinder"),

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
			"org.csstudio.ams.preferences.jmsAmsConnectionFactory"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_CONNECTION_FACTORY_CLASS(
			"org.csstudio.ams.preferences.jmsExternConnectionFactoryClass"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_1("org.csstudio.ams.preferences.jmsAmsProviderUrl1",
			"First provider URL"),

	/**
	 * XXX ?
	 */
	P_JMS_AMS_PROVIDER_URL_2("org.csstudio.ams.preferences.jmsAmsProviderUrl2",
			"Second provider URL"),

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
			"Subscriber ID of decission department to recieve commands");

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
