package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeSupport;

public class AlarmtopicBean extends AbstractConfigurationBean<AlarmtopicBean> {

	public static enum PropertyNames {
		topicID, topicName, humanReadableName, description

	}

	private int topicID;// PRIMARY KEY
	private String topicName;
	private String humanReadableName;
	private String description;

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

	public void copyStateOf(AlarmtopicBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	@Override
	public AlarmtopicBean getClone() {
		AlarmtopicBean bean = new AlarmtopicBean();
		bean.setDescription(getDescription());
		bean.setHumanReadableName(getHumanReadableName());
		bean.setTopicID(getTopicID());
		bean.setTopicName(getTopicName());
		return bean;
	}

	@Override
	public void updateState(AlarmtopicBean bean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet");

	}

	@Override
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
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime
				* result
				+ ((humanReadableName == null) ? 0 : humanReadableName
						.hashCode());
		result = prime * result
				+ ((topicName == null) ? 0 : topicName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
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
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}
}
