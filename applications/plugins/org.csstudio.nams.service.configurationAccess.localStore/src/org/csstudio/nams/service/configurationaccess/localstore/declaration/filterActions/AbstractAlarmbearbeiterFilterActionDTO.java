package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public class AbstractAlarmbearbeiterFilterActionDTO extends FilterActionDTO {

	public AlarmbearbeiterDTO getReceiver() {
		return (AlarmbearbeiterDTO) receiver;
	}

	public void setReceiver(AlarmbearbeiterDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserId());
	}
	
}
