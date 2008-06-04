package org.csstudio.nams.service.configurationaccess.localstore.configurationElements;

public final class TopicConfigurationId {

	private final int id;

	private TopicConfigurationId(int id) {
		this.id = id;
		// TODO Auto-generated constructor stub
	}

	public static TopicConfigurationId valueOf(int id) {
		return new TopicConfigurationId(id);
	}

	public int asDatabaseId(){
		return id;
	}
}
