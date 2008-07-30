package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;

@Entity
@DiscriminatorValue("10")
public class TopicFilterActionDTO extends FilterActionDTO {

	public TopicFilterActionDTO() {
		filterActionType = AlarmTopicFilterActionType.TOPIC;
	}

	public void setReceiver(TopicDTO receiver) {
		this.setIReceiverRef(receiver.getId());
		this.receiver = receiver;
	}

	public TopicDTO getReceiver() {
		return (TopicDTO) receiver;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {

	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		this.setReceiver(mapper.findForId(TopicDTO.class, this
				.getIReceiverRef(), false));
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {

	}

}
