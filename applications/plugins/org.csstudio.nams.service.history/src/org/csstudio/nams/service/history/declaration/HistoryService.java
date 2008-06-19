package org.csstudio.nams.service.history.declaration;


public interface HistoryService {
	public void logReceivedReplicationDoneMessage();
	public void logReceivedStartReplicationMessage();
	public void logTimeOutForTimeBased(int regelwerkID, int messageDescId, int regelId);
}
