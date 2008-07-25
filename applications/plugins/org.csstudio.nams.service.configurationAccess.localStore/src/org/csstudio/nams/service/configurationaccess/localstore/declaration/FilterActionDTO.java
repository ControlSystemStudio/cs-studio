package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;

@Entity
@Table(name = "AMS_FILTERACTION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="IFILTERACTIONTYPEREF", discriminatorType=DiscriminatorType.INTEGER)
public abstract class FilterActionDTO implements NewAMSConfigurationElementDTO {

	@SuppressWarnings("unused")
	@Id
	@Column(name="IFILTERACTIONID", nullable=false)
	private int iFilterActionID;
	
	/**
	 * Entweder TOPIC, Alarmbearbeiter, AlarmbearbeiterGruppe
	 */
	@Transient
	protected NewAMSConfigurationElementDTO receiver;
	
	@Column(name="IRECEIVERREF", nullable=false)
	private int iReceiverRef;
	
	@Column(name="CMESSAGE", length=1024)
	private String message;
	
	@Transient
	protected FilterActionType filterActionType;
	
	public FilterActionType getFilterActionType(){
		if (filterActionType == null){
			throw new RuntimeException("filterActionType in "+ this + " not set");
		}return filterActionType;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	protected int getIReceiverRef() {
		return iReceiverRef;
	}
	protected void setIReceiverRef(int receiverRef) {
		this.iReceiverRef = receiverRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iFilterActionID;
		result = prime * result + iReceiverRef;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FilterActionDTO))
			return false;
		final FilterActionDTO other = (FilterActionDTO) obj;
		if (iFilterActionID != other.iFilterActionID)
			return false;
		if (iReceiverRef != other.iReceiverRef)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
	
	public String getUniqueHumanReadableName() {
		return toString();
	}
	
	public boolean isInCategory(int categoryDBId) {
		return false;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": Nachricht an Topic mit id: "+receiver;
	}
}
