package org.csstudio.nams.configurator.beans;


public class AlarmtopicBean extends AbstractConfigurationBean<AlarmtopicBean> implements IReceiverBean{

	public static enum PropertyNames {
		topicID, topicName, humanReadableName, description

	}

	private int topicID;
	private String topicName = "";
	private String humanReadableName = "";
	private String description = "";

	public AlarmtopicBean() {
		topicID = -1;
	}

	public int getTopicID() {
		return topicID;
	}

	public void setTopicID(int topicID) {
		int oldValue = getTopicID();
		this.topicID = topicID;
		pcs.firePropertyChange(
				PropertyNames.topicID.name(), oldValue,
				getTopicID());
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		String oldValue = getTopicName();
		this.topicName = topicName;
		pcs.firePropertyChange(
				PropertyNames.topicName.name(), oldValue,
				getTopicName());
	}

	public String getHumanReadableName() {
		return humanReadableName;
	}

	public void setHumanReadableName(String humanReadableName) {
		String oldValue = getHumanReadableName();
		this.humanReadableName = humanReadableName;
		pcs.firePropertyChange(
				PropertyNames.humanReadableName.name(), oldValue,
				getHumanReadableName());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldValue = getDescription();
		this.description = description;
		pcs.firePropertyChange(
				PropertyNames.description.name(), oldValue,
				getDescription());
	}

	public String getDisplayName() {
		return getHumanReadableName() != null ? getHumanReadableName()
				: "(ohne Namen)";
	}

	@Override
	protected void doUpdateState(AlarmtopicBean bean) {
		setDescription(bean.getDescription());
		setHumanReadableName(bean.getHumanReadableName());
		setTopicID(bean.getTopicID());
		setTopicName(bean.getTopicName());
	}

	public int getID() {
		return this.getTopicID();
	}

	@Override
	public String toString() {
		return getHumanReadableName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime
				* result
				+ ((humanReadableName == null) ? 0 : humanReadableName
						.hashCode());
		result = prime * result + topicID;
		result = prime * result
				+ ((topicName == null) ? 0 : topicName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AlarmtopicBean other = (AlarmtopicBean) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (humanReadableName == null) {
			if (other.humanReadableName != null)
				return false;
		} else if (!humanReadableName.equals(other.humanReadableName))
			return false;
		if (topicID != other.topicID)
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}

	public void setID(int id) {
		setTopicID(id);
	}
}
