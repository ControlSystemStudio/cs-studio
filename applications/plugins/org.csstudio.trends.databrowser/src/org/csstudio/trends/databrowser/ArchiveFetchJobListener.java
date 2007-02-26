package org.csstudio.trends.databrowser;

public interface ArchiveFetchJobListener 
{
	 void errorOccured(int errorId);
	 
	 void updateDone(boolean success);
}
