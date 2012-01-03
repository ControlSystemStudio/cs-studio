
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;

public abstract class AbstAlarmbFilterActionDTO extends
		FilterActionDTO {

	public AlarmbearbeiterDTO getReceiver() {
		return (AlarmbearbeiterDTO) _receiver;
	}

	public void setReceiver(AlarmbearbeiterDTO receiver) {
		this._receiver = receiver;
		this.setIReceiverRef(receiver.getUserId());
	}

	@Override
    public void deleteJoinLinkData(Mapper mapper) throws Throwable {
	    // Not used
	}

	@Override
    public void loadJoinData(Mapper mapper) throws Throwable {
		this.setReceiver(mapper.findForId(AlarmbearbeiterDTO.class, this
				.getIReceiverRef(), false));
	}

	@Override
    public void storeJoinLinkData(Mapper mapper) throws Throwable {
        // Not used
	}
}
