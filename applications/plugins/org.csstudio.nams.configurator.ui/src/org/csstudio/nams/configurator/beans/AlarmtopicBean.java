
package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.Messages;

public class AlarmtopicBean extends AbstractConfigurationBean<AlarmtopicBean>
		implements IReceiverBean {

	public static enum PropertyNames {
		topicID, topicName, humanReadableName, description

	}

	private int topicID;
	private String topicName = ""; //$NON-NLS-1$
	private String humanReadableName = ""; //$NON-NLS-1$
	private String description = ""; //$NON-NLS-1$

	public AlarmtopicBean() {
		this.topicID = -1;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AlarmtopicBean other = (AlarmtopicBean) obj;
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.humanReadableName == null) {
			if (other.humanReadableName != null) {
				return false;
			}
		} else if (!this.humanReadableName.equals(other.humanReadableName)) {
			return false;
		}
		if (this.topicID != other.topicID) {
			return false;
		}
		if (this.topicName == null) {
			if (other.topicName != null) {
				return false;
			}
		} else if (!this.topicName.equals(other.topicName)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
    public String getDisplayName() {
		return this.getHumanReadableName() != null ? this
				.getHumanReadableName() : Messages.AlarmtopicBean_without_name;
	}

	public String getHumanReadableName() {
		return this.humanReadableName;
	}

	@Override
    public int getID() {
		return this.getTopicID();
	}

	public int getTopicID() {
		return this.topicID;
	}

	public String getTopicName() {
		return this.topicName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((this.description == null) ? 0 : this.description.hashCode());
		result = prime
				* result
				+ ((this.humanReadableName == null) ? 0
						: this.humanReadableName.hashCode());
		result = prime * result + this.topicID;
		result = prime * result
				+ ((this.topicName == null) ? 0 : this.topicName.hashCode());
		return result;
	}

	public void setDescription(final String description) {
		final String oldValue = this.getDescription();
		this.description = (description != null) ? description : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.description.name(), oldValue,
				this.getDescription());
	}

	public void setHumanReadableName(final String humanReadableName) {
		final String oldValue = this.getHumanReadableName();
		this.humanReadableName = (humanReadableName != null) ? humanReadableName
				: ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.humanReadableName.name(),
				oldValue, this.getHumanReadableName());
	}

	@Override
    public void setID(final int id) {
		this.setTopicID(id);
	}

	public void setTopicID(final int topicID) {
		final int oldValue = this.getTopicID();
		this.topicID = topicID;
		this.pcs.firePropertyChange(PropertyNames.topicID.name(), oldValue,
				this.getTopicID());
	}

	public void setTopicName(final String topicName) {
		final String oldValue = this.getTopicName();
		this.topicName = (topicName != null) ? topicName : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.topicName.name(), oldValue,
				this.getTopicName());
	}

	@Override
	public String toString() {
		return this.getHumanReadableName();
	}

	@Override
	protected void doUpdateState(final AlarmtopicBean bean) {
		this.setDescription(bean.getDescription());
		this.setHumanReadableName(bean.getHumanReadableName());
		this.setTopicID(bean.getTopicID());
		this.setTopicName(bean.getTopicName());
	}

	@Override
    public void setDisplayName(String name) {
		this.setHumanReadableName(name);
	}
}
