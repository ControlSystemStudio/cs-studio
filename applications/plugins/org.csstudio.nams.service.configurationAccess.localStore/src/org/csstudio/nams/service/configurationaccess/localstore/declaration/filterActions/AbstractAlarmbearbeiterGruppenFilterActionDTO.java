package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstractAlarmbearbeiterGruppenFilterActionDTO extends FilterActionDTO {

	public AlarmbearbeiterGruppenDTO getReceiver() {
		return (AlarmbearbeiterGruppenDTO) receiver;
	}

	public void setReceiver(AlarmbearbeiterGruppenDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserGroupId());
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen = mapper.loadAll(AlarmbearbeiterGruppenDTO.class, false);

		for (AlarmbearbeiterGruppenDTO alarmbearbeiterGruppe : alleAlarmbearbeiterGruppen) {
			if( alarmbearbeiterGruppe.getUserGroupId() == this.getIReceiverRef() ) {
				this.setReceiver(alarmbearbeiterGruppe);
				break;
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
		
	}
	
}
