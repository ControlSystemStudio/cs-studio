package org.csstudio.nams.service.preferenceservice.declaration;

public enum PreferenceServiceJMSKeys implements HoldsAPreferenceId {
	P_JMS_EXTERN_CONNECTION_FACTORY("org.csstudio.ams.preferences.jmsExternConnectionFactory"),
	P_JMS_EXTERN_CONNECTION_FACTORY_CLASS("org.csstudio.ams.preferences.jmsExternConnectionFactoryClass"),
	P_JMS_EXTERN_SENDER_PROVIDER_URL("org.csstudio.ams.preferences.jmsExternSenderProviderUrl"),
	P_JMS_EXTERN_PROVIDER_URL_1("org.csstudio.ams.preferences.jmsExternProviderUrl1"),
	P_JMS_EXTERN_PROVIDER_URL_2("org.csstudio.ams.preferences.jmsExternProviderUrl2"),
	
	P_JMS_EXT_TOPIC_STATUSCHANGE("org.csstudio.ams.preferences.jmsExternTopicStatusChange"),
	P_JMS_EXT_TOPIC_ALARM("org.csstudio.ams.preferences.jmsExternTopicAlarm"),
	P_JMS_EXT_TOPIC_COMMAND("org.csstudio.ams.preferences.jmsExternTopicCommand"),
	
	P_JMS_AMS_TOPIC_SMS_CONNECTOR("org.csstudio.ams.preferences.jmsAmsTopicSmsConnector"),
	P_JMS_AMS_TOPIC_JMS_CONNECTOR("org.csstudio.ams.preferences.jmsAmsTopicJMSConnector"),
	P_JMS_AMS_TOPIC_EMAIL_CONNECTOR("org.csstudio.ams.preferences.jmsAmsTopicEMailConnector"),
	P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR("org.csstudio.ams.preferences.jmsAmsTopicVoiceMailConnector"),
	P_JMS_AMS_TOPIC_DISTRIBUTOR("org.csstudio.ams.preferences.jmsAmsTopicDistributor"),
	P_JMS_AMS_TOPIC_REPLY("org.csstudio.ams.preferences.jmsAmsTopicReply"),
	P_JMS_AMS_TOPIC_MESSAGEMINDER("org.csstudio.ams.preferences.jmsAmsTopicMessageMinder"),
	P_JMS_AMS_TOPIC_COMMAND("org.csstudio.ams.preferences.jmsAmsTopicCommand"),
	
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS("org.csstudio.ams.preferences.jmsFreeTopicConnectionFactoryClass"),
	P_JMS_FREE_TOPIC_CONNECTION_FACTORY("org.csstudio.ams.preferences.jmsFreeTopicConnectionFactory"),
	
	P_JMS_AMS_CONNECTION_FACTORY("org.csstudio.ams.preferences.jmsAmsConnectionFactory"),
	P_JMS_AMS_CONNECTION_FACTORY_CLASS("org.csstudio.ams.preferences.jmsAmsConnectionFactoryClass"),
	P_JMS_AMS_PROVIDER_URL_1("org.csstudio.ams.preferences.jmsAmsProviderUrl1"),
	P_JMS_AMS_PROVIDER_URL_2("org.csstudio.ams.preferences.jmsAmsProviderUrl2"),
	P_JMS_AMS_SENDER_PROVIDER_URL("org.csstudio.ams.preferences.jmsAmsSenderProviderUrl");
	
	private String _key;
	private String _description;

	private PreferenceServiceJMSKeys(String key, String description) {
		_key = key;
		_description = description; 
	}
	
	private PreferenceServiceJMSKeys(String key) {
		this(key, "No description available");
	}
	
	public String getKey() {
		return _key;
	}
	
	public String getDescription() {
		return _description;
	}

	public String getPreferenceStoreId() {
		return _key;
	}

}
