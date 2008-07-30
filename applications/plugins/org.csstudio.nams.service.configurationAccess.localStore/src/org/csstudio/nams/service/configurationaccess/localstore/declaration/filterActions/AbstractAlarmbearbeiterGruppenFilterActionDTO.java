package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstractAlarmbearbeiterGruppenFilterActionDTO extends
		FilterActionDTO {

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub

	}

	public AlarmbearbeiterGruppenDTO getReceiver() {
		return (AlarmbearbeiterGruppenDTO) this.receiver;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		final List<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen = mapper
				.loadAll(AlarmbearbeiterGruppenDTO.class, false);

		for (final AlarmbearbeiterGruppenDTO alarmbearbeiterGruppe : alleAlarmbearbeiterGruppen) {
			if (alarmbearbeiterGruppe.getUserGroupId() == this
					.getIReceiverRef()) {
				this.setReceiver(alarmbearbeiterGruppe);
				break;
			}
		}
	}

	public void setReceiver(final AlarmbearbeiterGruppenDTO receiver) {
		this.receiver = receiver;
		this.setIReceiverRef(receiver.getUserGroupId());
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub

	}

}
