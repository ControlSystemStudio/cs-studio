package org.csstudio.alarm.table.database;

import java.io.File;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class AsynchronousDatabaseAccess {

    public void readMessages(final IDatabaseAccessListener listener,
            final Filter filter) {
        final Filter newFilter = filter.copy();
        Job readJob = new Job("Reader") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                ILogMessageArchiveAccess adba = ArchiveDBAccess.getInstance();
                result.setMessagesFromDatabase(adba.getLogMessages(newFilter
                        .getFrom(), newFilter.getTo(), newFilter.getFilterItems(),
                        filter.getMaximumMessageSize()));
                result.setMaxSize(adba.is_maxSize());
                listener.onReadFinished(result);
                return Status.OK_STATUS;
            }
        };
        readJob.schedule();
    }

    public void exportMessagesInFile(final IDatabaseAccessListener listener,
            final Filter filter, final File filePath, final String[] columnNames) {
        final Filter newFilter = filter.copy();
        Job exportJob = new Job("Export") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                ILogMessageArchiveAccess adba = ArchiveDBAccess.getInstance();
                result.setAccessResult(adba
                        .exportLogMessages(newFilter.getFrom(), newFilter.getTo(),
                                newFilter.getFilterItems(), newFilter
                                        .getMaximumMessageSize(), filePath,
                                columnNames));
                result.setMaxSize(adba.is_maxSize());
                listener.onExportFinished(result);
                return Status.OK_STATUS;
            }
        };
        exportJob.schedule();
    }

    public void countMessages(final IDatabaseAccessListener listener,
            final Filter filter) {
        final Filter newFilter = filter.copy();
        Job countJob = new Job("CountMessages") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                ILogMessageArchiveAccess adba = ArchiveDBAccess.getInstance();
                result.setMsgNumber(adba.countDeleteLogMessages(newFilter
                        .getFrom(), newFilter.getTo(), newFilter.getFilterItems()));
                listener.onMessageCountFinished(result);
                return Status.OK_STATUS;
            }
        };
        countJob.schedule();
    }

    public void deleteMessages(final IDatabaseAccessListener listener,
            final Filter filter) {
        final Filter newFilter = filter.copy();
        Job deleteJob = new Job("DeleteMessages") {
            protected IStatus run(IProgressMonitor monitor) {
                Result result = new Result();
                ILogMessageArchiveAccess adba = ArchiveDBAccess.getInstance();
                result.setAccessResult(adba.deleteLogMessages(newFilter
                        .getFrom(), newFilter.getTo(), newFilter.getFilterItems()));
                listener.onDeletionFinished(result);
                return Status.OK_STATUS;
            }
        };
        deleteJob.schedule();
    }

}
