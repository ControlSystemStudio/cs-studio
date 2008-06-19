package org.csstudio.nams.service.history.impl.confstore;

import java.util.Date;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.history.declaration.HistoryService;

public class HistoryServiceImpl implements HistoryService {

	private final LocalStoreConfigurationService localStoreConfigurationService;

	public HistoryServiceImpl(
			LocalStoreConfigurationService localStoreConfigurationService) {
				this.localStoreConfigurationService = localStoreConfigurationService;
	}
	public void logReceivedReplicationDoneMessage() {
		
		HistoryDTO historyDTO= prepareReplicationHistoryDTO();
		historyDTO.setCDescription("Filtermanager stops normal work, wait for Distributor.");
		localStoreConfigurationService.saveHistoryDTO(historyDTO);
	}
	public void logReceivedStartReplicationMessage() {
		HistoryDTO historyDTO= prepareReplicationHistoryDTO();
		historyDTO.setCDescription("Filtermanager got config replication end, goes to normal work.");
		localStoreConfigurationService.saveHistoryDTO(historyDTO);
	}
	
	private HistoryDTO prepareReplicationHistoryDTO() {
		HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setTTimeNew(new Date(System.currentTimeMillis()).getTime());
		historyDTO.setCActionType("Config Synch");
		return historyDTO;
	}
	
	public void logTimeOutForTimeBased(int regelwerkId, int messageDescId, int regelId)
 {
		HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setTTimeNew(new Date(System.currentTimeMillis()).getTime());
		historyDTO.setCActionType("TimeBased");
		historyDTO.setCDescription("Timeout for Msg "
				+ messageDescId + " (FC="
				+ regelId + "/F="
				+ regelwerkId + ")");
		localStoreConfigurationService.saveHistoryDTO(historyDTO);
	}

}
