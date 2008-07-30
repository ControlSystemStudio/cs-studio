package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstractAlarmbearbeiterFilterActionDTO extends
		FilterActionDTO {

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub

	}

	public AlarmbearbeiterDTO getReceiver() {
		return (AlarmbearbeiterDTO) this.receiver;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		final List<AlarmbearbeiterDTO> alleAlarmbearbeiter = mapper.loadAll(
				AlarmbearbeiterDTO.class, true);

		for (final AlarmbearbeiterDTO alarmbearbeiter : alleAlarmbearbeiter) {
			if (alarmbearbeiter.getUserId() == this.getIReceiverRef()) {
				this.setReceiver(alarmbearbeiter);
				break;
			}
		}
	}

	public void setReceiver(final AlarmbearbeiterDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserId());
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub

	}

}
