
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

@Deprecated
public final class TopicConfigurationId {

	public static TopicConfigurationId valueOf(final int id) {
		return new TopicConfigurationId(id);
	}

	private final int _id;

	private TopicConfigurationId(final int id) {
		this._id = id;
		// TODO Auto-generated constructor stub
	}

	public int asDatabaseId() {
		return this._id;
	}
}
