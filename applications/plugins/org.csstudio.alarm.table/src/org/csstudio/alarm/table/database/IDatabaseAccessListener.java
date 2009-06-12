package org.csstudio.alarm.table.database;

import org.csstudio.alarm.dbaccess.archivedb.Result;



public interface IDatabaseAccessListener {

    public void onReadFinished(Result result);
    
    public void onDeletionFinished(Result result);
    
    public void onExportFinished(Result result);

    public void onMessageCountFinished(Result result);
}
