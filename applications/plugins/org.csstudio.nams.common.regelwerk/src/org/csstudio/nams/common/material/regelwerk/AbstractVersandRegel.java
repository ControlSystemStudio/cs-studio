package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.service.history.declaration.HistoryService;

public abstract class AbstractVersandRegel implements VersandRegel{
	protected HistoryService historyService;

	public void setHistoryService(HistoryService historyService){
		this.historyService = historyService;
	}

	protected HistoryService getHistoryService() {
		return historyService;
	}
}
