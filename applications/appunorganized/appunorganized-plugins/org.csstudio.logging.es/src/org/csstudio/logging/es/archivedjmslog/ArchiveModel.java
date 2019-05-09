package org.csstudio.logging.es.archivedjmslog;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/** Model representing data in an archive, typically some sort of database. */
public abstract class ArchiveModel<T extends LogMessage> extends Model
{
    private final Set<ArchiveModelListener<T>> listeners = Collections
            .newSetFromMap(new WeakHashMap<ArchiveModelListener<T>, Boolean>());

    public void addListener(ArchiveModelListener<T> listener)
    {
        Activator.checkParameter(listener, "listener"); //$NON-NLS-1$
        synchronized (this.listeners)
        {
            this.listeners.add(listener);
        }
    }

    public abstract T[] getMessages();

    /**
     * Retrieve archived data from the given time interval.
     * 
     * On completion of the query, the caller will be notified via the
     * {@link ArchiveModelListener#messagesRetrieved(ArchiveModel)} callback.
     * 
     * @param from
     *            The start time.
     * @param to
     *            The end time.
     * @param maxResults
     *            The maximum number of results to fetch. The most recent
     *            results within the time interval will be returned. Set to 0 to
     *            return all results in the defined time interval.
     */
    public abstract void refresh(Instant from, Instant to, long maxResults);

    public void removeListener(ArchiveModelListener<T> listener)
    {
        synchronized (this.listeners)
        {
            this.listeners.remove(listener);
        }
    }

    protected void sendCompletionNotification()
    {
        synchronized (this.listeners)
        {
            this.listeners.forEach((r) -> {
                try
                {
                    r.messagesRetrieved(this);
                }
                catch (Throwable ex)
                {
                    Activator.getLogger()
                            .warning("Notification failed: " + ex.getMessage()); //$NON-NLS-1$
                    ex.printStackTrace();
                }
            });
        }
    }
}
