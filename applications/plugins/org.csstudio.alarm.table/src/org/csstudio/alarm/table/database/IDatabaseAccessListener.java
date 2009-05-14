package org.csstudio.alarm.table.database;



public interface IDatabaseAccessListener {

    public void onReadFinished(Result result);
    
    public void onDeletionFinished(Result result);
    
    public void onExportFinished(Result result);

    public void onMessageCountFinished(Result result);
}
