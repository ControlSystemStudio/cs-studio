
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "AMS_DEFMESSAGETEXT")
public class DefaultFilterTextDTO implements NewAMSConfigurationElementDTO {

	@Column(name = "IDEFMESSAGETEXTID")
	@Id
	private int textID;

	@Column(name = "CNAME", length = 128)
	private String messageName;

	@Column(name = "CTEXT", length = 1024)
	private String text;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DefaultFilterTextDTO)) {
			return false;
		}
		final DefaultFilterTextDTO other = (DefaultFilterTextDTO) obj;
		if (this.messageName == null) {
			if (other.messageName != null) {
				return false;
			}
		} else if (!this.messageName.equals(other.messageName)) {
			return false;
		}
		if (this.text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!this.text.equals(other.text)) {
			return false;
		}
		if (this.textID != other.textID) {
			return false;
		}
		return true;
	}

	public String getMessageName() {
		return this.messageName;
	}

	public String getText() {
		return this.text;
	}

	@Override
    public String getUniqueHumanReadableName() {
		return this.messageName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.messageName == null) ? 0 : this.messageName.hashCode());
		result = prime * result
				+ ((this.text == null) ? 0 : this.text.hashCode());
		result = prime * result + this.textID;
		return result;
	}

	@Override
    public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void setMessageName(final String messageName) {
		this.messageName = messageName;
	}

	public void setText(final String text) {
		this.text = text;
	}

}
