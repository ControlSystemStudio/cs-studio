package org.csstudio.nams.service.history.impl.confstore;

import java.util.Date;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.declaration.HistoryStorageException;

public class HistoryServiceImpl implements HistoryService {

	private final LocalStoreConfigurationService localStoreConfigurationService;

	public HistoryServiceImpl(
			LocalStoreConfigurationService localStoreConfigurationService) {
		this.localStoreConfigurationService = localStoreConfigurationService;
	}

	public void logReceivedReplicationDoneMessage() {

		HistoryDTO historyDTO = prepareReplicationHistoryDTO();
		historyDTO
				.setCDescription("Filtermanager stops normal work, wait for Distributor.");
		try {
			localStoreConfigurationService.saveHistoryDTO(historyDTO);
		} catch (StorageError e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (StorageException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (InconsistentConfigurationException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		}
	}

	public void logReceivedStartReplicationMessage() {
		HistoryDTO historyDTO = prepareReplicationHistoryDTO();
		historyDTO
				.setCDescription("Filtermanager got config replication end, goes to normal work.");
		try {
			localStoreConfigurationService.saveHistoryDTO(historyDTO);
		} catch (StorageError e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (StorageException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (InconsistentConfigurationException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		}
	}

	private HistoryDTO prepareReplicationHistoryDTO() {
		HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setTTimeNew(new Date(System.currentTimeMillis()).getTime());
		historyDTO.setCActionType("Config Synch");
		return historyDTO;
	}

	public void logTimeOutForTimeBased(Vorgangsmappe vorgangsmappe) {
		Regelwerkskennung regelwerkId = vorgangsmappe.gibPruefliste()
				.gibRegelwerkskennung();
		AlarmNachricht alarmNachricht = vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges();

		HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setTTimeNew(new Date(System.currentTimeMillis()).getTime());
		historyDTO.setCActionType("TimeBased");
		historyDTO.setCDescription("Timeout for Msg "
				+ alarmNachricht.toString()
				// FIXME Hallo Desy'aner, bitte entscheidet, was hier in die
				// history geschrieben werden soll!?
				// + messageDescId
				+ " ("
				// + "FC="
				// + regelId
				// + "/"
				+ "F=" + regelwerkId.getRegelwerksId() + ")");
		try {
			localStoreConfigurationService.saveHistoryDTO(historyDTO);
		} catch (StorageError e) {
			throw new HistoryStorageException("could not log time-out message",
					e);
		} catch (StorageException e) {
			throw new HistoryStorageException("could not log time-out message",
					e);
		} catch (InconsistentConfigurationException e) {
			throw new HistoryStorageException("could not log time-out message",
					e);
		}
	}

}
