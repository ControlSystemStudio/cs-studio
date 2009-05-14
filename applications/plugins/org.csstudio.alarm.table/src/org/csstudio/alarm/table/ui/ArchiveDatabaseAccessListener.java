package org.csstudio.alarm.table.ui;

import org.csstudio.alarm.table.database.IDatabaseAccessListener;
import org.csstudio.alarm.table.database.Result;

public abstract class ArchiveDatabaseAccessListener implements IDatabaseAccessListener {

    public void onReadFinished(Result result) {
    }
    
    public void onDeletionFinished(Result result) {
        // TODO Auto-generated method stub
    }
    
    public void onExportFinished(Result result) {
        // TODO Auto-generated method stub
    }
    
    public void onMessageCountFinished(Result result) {
        
    }
}
