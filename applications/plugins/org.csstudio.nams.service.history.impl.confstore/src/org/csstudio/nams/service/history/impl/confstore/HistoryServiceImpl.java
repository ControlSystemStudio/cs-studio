
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
			final LocalStoreConfigurationService localStoreConfigurationService) {
		this.localStoreConfigurationService = localStoreConfigurationService;
	}

	@Override
    public void logReceivedStartReplicationMessage() {

		final HistoryDTO historyDTO = this.prepareReplicationHistoryDTO();
		historyDTO
				.setCDescription("Filtermanager stops normal work, wait for Distributor.");
		try {
			this.localStoreConfigurationService.saveHistoryDTO(historyDTO);
		} catch (final StorageError e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (final StorageException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (final InconsistentConfigurationException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		}
	}

	@Override
    public void logReceivedReplicationDoneMessage() {
		final HistoryDTO historyDTO = this.prepareReplicationHistoryDTO();
		historyDTO
				.setCDescription("Filtermanager got config replication end, goes to normal work.");
		try {
			this.localStoreConfigurationService.saveHistoryDTO(historyDTO);
		} catch (final StorageError e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (final StorageException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		} catch (final InconsistentConfigurationException e) {
			throw new HistoryStorageException(
					"could not log replication done message", e);
		}
	}

	@Override
    public void logTimeOutForTimeBased(final Vorgangsmappe vorgangsmappe) {
		final Regelwerkskennung regelwerkId = vorgangsmappe.gibPruefliste()
				.gibRegelwerkskennung();
		final AlarmNachricht alarmNachricht = vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges();

		final HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setTTimeNewAsDate(new Date());
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
			this.localStoreConfigurationService.saveHistoryDTO(historyDTO);
		} catch (final StorageError e) {
			throw new HistoryStorageException("could not log time-out message",
					e);
		} catch (final StorageException e) {
			throw new HistoryStorageException("could not log time-out message",
					e);
		} catch (final InconsistentConfigurationException e) {
			throw new HistoryStorageException("could not log time-out message",
					e);
		}
	}

	private HistoryDTO prepareReplicationHistoryDTO() {
		final HistoryDTO historyDTO = new HistoryDTO();
		historyDTO.setTTimeNewAsDate(new Date());
		historyDTO.setCType("Config Synch");
		return historyDTO;
	}

}
