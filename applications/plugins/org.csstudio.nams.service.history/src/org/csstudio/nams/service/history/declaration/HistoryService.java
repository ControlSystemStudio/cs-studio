package org.csstudio.nams.service.history.declaration;

public interface HistoryService {
	public void logStoppingServiceForReplication();
	public void logReceivedReplicationDoneMessage();
	public void logTimeOutForTimeBased();
}
