package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstractAlarmbearbeiterFilterActionDTO extends
		FilterActionDTO {

	public AlarmbearbeiterDTO getReceiver() {
		return (AlarmbearbeiterDTO) receiver;
	}

	public void setReceiver(AlarmbearbeiterDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserId());
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {

	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		this.setReceiver(mapper.findForId(AlarmbearbeiterDTO.class, this
				.getIReceiverRef(), false));
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {

	}

}
