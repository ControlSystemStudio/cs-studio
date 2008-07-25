package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public class AbstractAlarmbearbeiterGruppenFilterActionDTO extends FilterActionDTO {

	public AlarmbearbeiterGruppenDTO getReceiver() {
		return (AlarmbearbeiterGruppenDTO) receiver;
	}

	public void setReceiver(AlarmbearbeiterGruppenDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserGroupId());
	}
	
}
