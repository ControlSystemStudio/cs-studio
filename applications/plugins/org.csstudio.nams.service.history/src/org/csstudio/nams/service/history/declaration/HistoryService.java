
package org.csstudio.nams.service.history.declaration;

import org.csstudio.nams.common.decision.Vorgangsmappe;

public interface HistoryService {
	public void logReceivedReplicationDoneMessage()
			throws HistoryStorageException;

	public void logReceivedStartReplicationMessage()
			throws HistoryStorageException;

	public void logTimeOutForTimeBased(Vorgangsmappe vorgangsmappe)
			throws HistoryStorageException;
}
