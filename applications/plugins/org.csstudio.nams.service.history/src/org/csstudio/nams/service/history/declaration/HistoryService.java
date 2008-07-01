package org.csstudio.nams.service.history.declaration;

import org.csstudio.nams.common.decision.Vorgangsmappe;


public interface HistoryService {
	public void logReceivedReplicationDoneMessage();
	public void logReceivedStartReplicationMessage();
	public void logTimeOutForTimeBased(Vorgangsmappe vorgangsmappe);
}
