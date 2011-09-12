
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstAlarmbGruppenFilterActionDTO extends
		FilterActionDTO {

	public AlarmbearbeiterGruppenDTO getReceiver() {
		return (AlarmbearbeiterGruppenDTO) _receiver;
	}

	public void setReceiver(AlarmbearbeiterGruppenDTO receiver) {
		this._receiver = receiver;
		this.setIReceiverRef(receiver.getUserGroupId());
	}

	@Override
    public void deleteJoinLinkData(Mapper mapper) throws Throwable {
	    // Not used
	}

	@Override
    public void loadJoinData(Mapper mapper) throws Throwable {
		this.setReceiver(mapper.findForId(AlarmbearbeiterGruppenDTO.class, this
				.getIReceiverRef(), false));
	}

	@Override
    public void storeJoinLinkData(Mapper mapper) throws Throwable {
	    // Not used
	}
}
