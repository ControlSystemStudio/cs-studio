package org.csstudio.logging.es.archivedjmslog;

public interface LiveModelListener<T extends LogMessage>
{
    public void newMessage(T msg);
}
