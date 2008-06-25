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
	private PropertyChangeSupport propertyChangeSupport;

	public AlarmtopicBean() {
		topicID = -1;
		propertyChangeSupport = getPropertyChangeSupport();
	}

	public int getTopicID() {
		return topicID;
	}

	public void setTopicID(int topicID) {
		int oldValue = getTopicID();
		this.topicID = topicID;
		propertyChangeSupport.firePropertyChange(
				PropertyNames.topicID.name(), oldValue,
				getTopicID());
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		String oldValue = getTopicName();
		this.topicName = topicName;
		propertyChangeSupport.firePropertyChange(
				PropertyNames.topicName.name(), oldValue,
				getTopicName());
	}

	public String getHumanReadableName() {
		return humanReadableName;
	}

	public void setHumanReadableName(String humanReadableName) {
		String oldValue = getHumanReadableName();
		this.humanReadableName = humanReadableName;
		propertyChangeSupport.firePropertyChange(
				PropertyNames.humanReadableName.name(), oldValue,
				getHumanReadableName());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldValue = getDescription();
		this.description = description;
		propertyChangeSupport.firePropertyChange(
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
}
