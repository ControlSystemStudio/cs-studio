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

	public TopicFilterActionDTO() {
		this.filterActionType = AlarmTopicFilterActionType.TOPIC;
	}

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub

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
		final TopicFilterActionDTO other = (TopicFilterActionDTO) obj;
		if (this.receiver == null) {
			if (other.receiver != null) {
				return false;
			}
		} else if (!this.receiver.equals(other.receiver)) {
			return false;
		}
		return true;
	}

	public TopicDTO getReceiver() {
		return (TopicDTO) this.receiver;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.receiver == null) ? 0 : this.receiver.hashCode());
		return result;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		final List<TopicDTO> alleTopics = mapper.loadAll(TopicDTO.class, true);

		for (final TopicDTO topic : alleTopics) {
			if (topic.getId() == this.getIReceiverRef()) {
				this.setReceiver(topic);
				break;
			}
		}
	}

	public void setReceiver(final TopicDTO receiver) {
		this.setIReceiverRef(receiver.getId());
		this.receiver = receiver;
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub

	}

}
