package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;


@Entity
@Table(name="AMS_DEFMESSAGETEXT")
public class DefaultFilterTextDTO implements NewAMSConfigurationElementDTO{

	@Column(name="IDEFMESSAGETEXTID")
	@Id
	private String textID;
	
	@Column(name="CNAME")
	private String messageName;
	
	@Column(name="CTEXT")
	private String text;
	
	public String getUniqueHumanReadableName() {
		return messageName;
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

	public String getTextID() {
		return textID;
	}

	public void setTextID(String textID) {
		this.textID = textID;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((messageName == null) ? 0 : messageName.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((textID == null) ? 0 : textID.hashCode());
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
		final DefaultFilterTextDTO other = (DefaultFilterTextDTO) obj;
		if (messageName == null) {
			if (other.messageName != null)
				return false;
		} else if (!messageName.equals(other.messageName))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (textID == null) {
			if (other.textID != null)
				return false;
		} else if (!textID.equals(other.textID))
			return false;
		return true;
	}

}
