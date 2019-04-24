package org.csstudio.logging.es.archivedjmslog;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.time.StartEndTimeParser;

/** Merges archived data with live data from JMS. */
public class MergedModel<T extends LogMessage>
        implements ArchiveModelListener<T>, LiveModelListener<T>
{
    protected ArchiveModel<T> archive;
    protected LiveModel<T> live;
    protected TreeSet<LogMessage> messages = new TreeSet<>();
    protected String startSpec = "-8 hour"; //$NON-NLS-1$
    protected String endSpec = RelativeTime.NOW;

    protected ScheduledExecutorService expireService;

    protected Set<MergedModelListener<T>> listeners = Collections
            .newSetFromMap(new WeakHashMap<MergedModelListener<T>, Boolean>());
    protected PropertyFilter[] filters;

    private Class<T> parameterType;

    @SuppressWarnings("unchecked")
    public MergedModel(ArchiveModel<T> archive, LiveModel<T> live)
    {
        this.archive = archive;
        if (null != this.archive)
        {
            this.archive.addListener(this);
        }
        this.live = live;
        if (null != this.live)
        {
            this.live.addListener(this);
        }

        this.expireService = Executors.newSingleThreadScheduledExecutor();
        this.expireService.scheduleAtFixedRate(this::expireMessages, 1, 1,
                TimeUnit.MINUTES);

        this.parameterType = ((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public void addListener(MergedModelListener<T> listener)
    {
        Activator.checkParameter(listener, "listener"); //$NON-NLS-1$
        synchronized (this.listeners)
        {
            this.listeners.add(listener);
        }
    }

    protected void expireMessages()
    {
        Long cutoff = null;
        try
        {
            final StartEndTimeParser times = new StartEndTimeParser(
                    this.startSpec, this.endSpec);
            cutoff = times.getStart().getTime().getTime();
        }
        catch (Exception e)
        {
            // shouldn't happen since we verify on setting changes
            e.printStackTrace();
            return;
        }
        long expired = 0L;
        synchronized (this.messages)
        {
            Iterator<LogMessage> i = this.messages.iterator();
            while (i.hasNext())
            {
                LogMessage msg = i.next();
                if (cutoff.compareTo(msg.getTime()) < 0)
                {
                    // the first message with a timestamp > cutoff.
                    // no need to look any further.
                    break;
                }
                i.remove();
                ++expired;
            }
        }
        Logger.getLogger(Activator.ID)
                .fine(String.format("%d messages expired.", expired)); //$NON-NLS-1$
        if (0 < expired)
        {
            fireModelChanged();
        }
    }

    protected void fireModelChanged()
    {
        synchronized (this.listeners)
        {
            for (MergedModelListener<T> l : this.listeners)
            {
                try
                {
                    l.onChange(this);
                }
                catch (Throwable e)
                {
                    Activator.getLogger()
                            .warning("Model change notification failed: " //$NON-NLS-1$
                                    + e.getMessage());
                }
            }
        }
    }

    protected void fireModelError(String error)
    {
        synchronized (this.listeners)
        {
            for (MergedModelListener<T> l : this.listeners)
            {
                try
                {
                    l.onError(this, error);
                }
                catch (Throwable e)
                {
                    Activator.getLogger()
                            .warning("Model error notification failed: " //$NON-NLS-1$
                                    + e.getMessage());
                }
            }
        }
    }

    public String getEndSpec()
    {
        return this.endSpec;
    }

    public PropertyFilter[] getFilters()
    {
        return this.filters;
    }

    protected void getFromArchive(Instant from, Instant to, long maxResults)
    {
        this.archive.refresh(from, to, maxResults);
    }

    @SuppressWarnings("unchecked")
    public T[] getMessages()
    {
        synchronized (this.messages)
        {
            return this.messages.toArray((T[]) Array
                    .newInstance(this.parameterType, this.messages.size()));
        }
    }

    public String getStartSpec()
    {
        return this.startSpec;
    }

    @Override
    public void messagesRetrieved(ArchiveModel<T> model)
    {
        this.messages.addAll(Arrays.asList(model.getMessages()));
        fireModelChanged();
    }

    @Override
    public void newMessage(T msg)
    {
        // ignore the message if we are not in "NOW" mode.
        if (!RelativeTime.NOW.equals(this.endSpec))
        {
            return;
        }
        synchronized (this.messages)
        {
            this.messages.add(msg);
        }
        fireModelChanged();
    }

    public void removeListener(MergedModelListener<T> listener)
    {
        synchronized (this.listeners)
        {
            this.listeners.remove(listener);
        }
    }

    /**
     * Define the filters to use when receiving messages.
     * 
     * The model will be updates from the archive.
     * 
     * @param filters
     *            The new filter definitions.
     */
    public void setFilters(PropertyFilter[] filters)
    {
        this.filters = filters;
        if (null != this.live)
        {
            this.live.setFilters(filters);
        }
        if (null != this.archive)
        {
            this.archive.setFilters(filters);
        }
        updateFromArchive();
    }

    /**
     * Define the time interval to represent in the model.
     * 
     * @param start_spec
     *            The start time.
     * @param end_spec
     *            The end time. Set to {@value RelativeTime#NOW} to enable the
     *            reception of live messages via JMS.
     */
    @SuppressWarnings("unused")
    public void setTimerange(final String start_spec, final String end_spec)
            throws Exception
    {
        Activator.checkParameterString(start_spec, "start_spec"); //$NON-NLS-1$
        Activator.checkParameterString(end_spec, "end_spec"); //$NON-NLS-1$
        // First parse, so that invalid inputs throw an Exception and the local
        // fields are not updated.
        new StartEndTimeParser(start_spec, end_spec);
        this.startSpec = start_spec;
        this.endSpec = end_spec;
        updateFromArchive();
    }

    /**
     * Trigger an update from the archive.
     */
    public void updateFromArchive()
    {
        this.messages.clear();
        if (null != this.live)
        {
            if (RelativeTime.NOW.equals(this.endSpec))
            {
                this.live.start();
            }
            else
            {
                this.live.stop();
            }
        }
        if (null != this.archive)
        {
            StartEndTimeParser parser;
            try
            {
                parser = new StartEndTimeParser(this.startSpec, this.endSpec);
                this.archive.refresh(parser.getStart().toInstant(),
                        parser.getEnd().toInstant(), 0 /* all */);
            }
            catch (Exception e)
            {
                // we verified the inputs with exactly the same call, so this
                // cannot happen.
            }
        }
    }
}
