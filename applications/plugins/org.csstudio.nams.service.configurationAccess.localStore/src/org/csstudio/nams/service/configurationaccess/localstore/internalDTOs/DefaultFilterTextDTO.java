package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

/**
 * Templates für messages in den Filtern.
 *
 * <pre>
 * create table AMS_DefMessageText
 * (
 * 	iDefMessageTextID	NUMBER(11)	NOT NULL,
 * 	cName			VARCHAR2(128) 	NOT NULL,
 * 	cText			VARCHAR2(1024)	NOT NULL,
 * 	PRIMARY KEY(iDefMessageTextID)
 * );
 * </pre>
 */
@Entity
@Table(name="AMS_DEFMESSAGETEXT")
public class DefaultFilterTextDTO implements NewAMSConfigurationElementDTO{

	@Column(name="IDEFMESSAGETEXTID")
	@Id
	@GeneratedValue
	private int textID;
	
	@Column(name="CNAME", length=128)
	private String messageName;
	
	@Column(name="CTEXT", length=1024)
	private String text;
	
	public String getUniqueHumanReadableName() {
		return messageName;
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
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
		result = prime * result + textID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefaultFilterTextDTO))
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
		if (textID != other.textID)
			return false;
		return true;
	}

	

}
