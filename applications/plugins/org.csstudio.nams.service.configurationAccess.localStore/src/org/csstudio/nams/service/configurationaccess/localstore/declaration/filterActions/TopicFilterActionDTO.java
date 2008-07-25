package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;

@Entity
@Table(name = "AMS_FILTERACTION")
@DiscriminatorValue("10")
public class TopicFilterActionDTO extends FilterActionDTO {

	@Transient
	private TopicDTO receiver;

	public void setReceiver(TopicDTO receiver) {
		this.setIReceiverRef(receiver.getId());
		this.receiver = receiver;
	}
	
	public TopicDTO getReceiver() {
		return receiver;
	}
	
	
	
	
	
	
	public String getUniqueHumanReadableName() {
		return toString();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

	
	@Override
	public String toString() {
		return "TopicFilterActionDTO: Nachricht an Topic mit id: "+receiver;
	}
}
