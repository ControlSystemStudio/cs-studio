package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;

@Entity
@DiscriminatorValue("10")
public class TopicFilterActionDTO extends FilterActionDTO {

	public TopicFilterActionDTO(){
		filterActionType = AlarmTopicFilterActionType.TOPIC;
	}
	
	public void setReceiver(TopicDTO receiver) {
		this.setIReceiverRef(receiver.getId());
		this.receiver = receiver;
	}
	
	public TopicDTO getReceiver() {
		return (TopicDTO) receiver;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((receiver == null) ? 0 : receiver.hashCode());
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
		final TopicFilterActionDTO other = (TopicFilterActionDTO) obj;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		return true;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<TopicDTO> alleTopics = mapper.loadAll(TopicDTO.class, true);

		for (TopicDTO topic : alleTopics) {
			if( topic.getId() == this.getIReceiverRef() ) {
				this.setReceiver(topic);
				break;
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}

}
