package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstractAlarmbearbeiterFilterActionDTO extends FilterActionDTO {

	public AlarmbearbeiterDTO getReceiver() {
		return (AlarmbearbeiterDTO) receiver;
	}

	public void setReceiver(AlarmbearbeiterDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserId());
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<AlarmbearbeiterDTO> alleAlarmbearbeiter = mapper.loadAll(AlarmbearbeiterDTO.class, true);

		for (AlarmbearbeiterDTO alarmbearbeiter : alleAlarmbearbeiter) {
			if( alarmbearbeiter.getUserId() == this.getIReceiverRef() ) {
				this.setReceiver(alarmbearbeiter);
				break;
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}
	
}
