package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstractAlarmbearbeiterGruppenFilterActionDTO extends
		FilterActionDTO {

	public AlarmbearbeiterGruppenDTO getReceiver() {
		return (AlarmbearbeiterGruppenDTO) receiver;
	}

	public void setReceiver(AlarmbearbeiterGruppenDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserGroupId());
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {

	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		this.setReceiver(mapper.findForId(AlarmbearbeiterGruppenDTO.class, this
				.getIReceiverRef(), false));
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {

	}

}
