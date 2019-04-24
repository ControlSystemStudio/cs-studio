package org.csstudio.logging.es.archivedjmslog;

public interface ArchiveModelListener<T extends LogMessage>
{
    public void messagesRetrieved(ArchiveModel<T> model);
}
